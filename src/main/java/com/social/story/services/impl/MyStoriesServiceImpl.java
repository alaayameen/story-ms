package com.social.story.services.impl;

import com.social.core.models.UserInfo;
import com.social.story.data.mongo.dao.StoryViewRepository;
import com.social.story.data.mongo.models.StoryView;
import com.social.story.data.mongo.models.StoryViewsCount;
import com.social.story.gateway.FollowApiClient;
import com.social.story.gateway.UsersApiClient;
import com.social.story.data.mongo.models.Hashtag;
import com.social.story.data.mongo.models.Story;
import com.social.story.convert.StoryConverter;
import com.social.story.data.mongo.dao.StoryRepository;
import com.social.story.mappers.*;
import com.social.story.services.FollowService;
import com.social.story.services.HashtagService;
import com.social.story.services.MyStoriesService;
import com.social.story.services.StoryViewService;
import com.social.story.utils.CacheManager;
import com.social.story.utils.StoryHelper;
import com.social.swagger.called.follow.model.ShareContentRequest;
import com.social.swagger.called.follow.model.ShareContentRequest.ContentTypeEnum;
import com.social.swagger.called.user.model.PublicUserInfo;
import com.social.swagger.model.story.AddStoryRequest;
import com.social.swagger.model.story.GetMyStoriesRespond;
import com.social.swagger.model.story.GetStorySeenByUsersRespond;
import com.social.swagger.model.story.PublicUserInfoRespond;
import com.social.swagger.model.story.StoryOwnerId;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * @author ayameen
 *
 */
@Log4j2
@Service
@AllArgsConstructor
public class MyStoriesServiceImpl implements MyStoriesService {

    StoryRepository storyRepository;
    
    StoryConverter storyConverter;

    StoryMapper storyMapper;

    HashtagService hashtagService;

    StoryHelper helper;
    
    CacheManager cacheManager;

    GetMyStoriesRespondMapper getMyStoriesRespondMapper;

    StoryViewService storyViewService;

    UsersApiClient usersApiClient;

    StoryViewRepository storyViewRepository;

    PublicUserInfoMapper publicUserInfoMapper;

    CustomPublicUserInfoMapper customPublicUserInfoMapper;

    FollowService followService;
    
    FollowApiClient followApiClient;

    PublicUserInfoRespondMapper publicUserInfoRespondMapper;

    /**
     * Add new user story
     *
     * @param userId user who adding this story
     * @param addStoryRequest story details to be added
     * @return Story Id
     */
    @Override
    public String addStory(String authorization, String userId, AddStoryRequest addStoryRequest) {
        Story story = storyMapper.toTarget(addStoryRequest);
        story.setUserId(userId);
        story.setSeenByIds(Collections.emptyList());
        populateHashtags(story);
        
		// Convert story video to hls, and update story mediaDetails url
		if (addStoryRequest.isToHls() && story.getMediaDetails().getMediaType().toUpperCase().equals("VIDEO")) {
			story = storyConverter.convertVideoToHls(story);
		}

		// set the expire date for story to be 1 day
		story.setExpireAt(Date.from(helper.getCurrentDateTime().plusHours(24).toInstant()));
		
        String StoryId = storyRepository.save(story).getId();
        //Call shareContent API in follow ms in order to update story last time in User follow
        ShareContentRequest shareContentRequest = new ShareContentRequest();
        shareContentRequest.setContentType(ContentTypeEnum.STORY);
        followApiClient.trackSharedContent(authorization, userId, shareContentRequest);
        
        return StoryId;
    }

    /**
     * Retrieve my stories, pageable
     *
     * @param userId logged in user
     * @param page page number to get
     * @param size page size
     * @return page of logged in user stories
     */
    @Override
    public GetMyStoriesRespond getMyStories(String authorization, String userId, Integer page, Integer size) {
        Pageable pageable = helper.pageable(page, size, Sort.by("createdDate").descending());
        LocalDateTime dateMinus24Hours = LocalDateTime.now().minusHours(24);
        Page<Story> stories = storyRepository.findByUserIdAndCreatedDtGreaterThanOrderByCreatedDtDesc(userId, dateMinus24Hours, pageable);

        populateSeenBy(authorization, stories.getContent());

        return getMyStoriesRespondMapper.toTarget(stories);
    }

    /**
     * Retrieve user's stories, support pagination
     *
     * @param userId logged in user
     * @param page page number to get
     * @param size page size
     * @return page of logged in user stories
     */
    @Override
    public GetMyStoriesRespond getUserStories(String authorization, String userId, Integer page, Integer size) {
        Pageable pageable = helper.pageable(page, size, Sort.by("createdDt").descending());
        LocalDateTime dateMinus24Hours = LocalDateTime.now().minusHours(24);
        Page<Story> stories = storyRepository.findByUserIdAndCreatedDtGreaterThanOrderByCreatedDtDesc(userId, dateMinus24Hours, pageable);

        populateNumberOfUsersSeenStories(authorization, stories.getContent());

        return getMyStoriesRespondMapper.toTarget(stories);
    }

	/**
	 * Retrieve stories seen users, support pagination
	 *
	 * @param storyId logged in user
	 * @param page    page number to get
	 * @param size    page size
	 * @return page of logged in user stories
	 */
	@Override
	public PublicUserInfoRespond getSeenUsersByStoryId(String authorization, String storyId, Integer page, Integer size) {
        if (!ObjectId.isValid(storyId)) {
            log.error("StoryId is invalid {}",  storyId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STORY_ID_INVALID");
        }
		Pageable pageable = helper.pageable(page, size, Sort.by("createdDt").descending());
		Page<StoryView> storyViewsList = storyViewService.findByStoryId(storyId, pageable);
		List<String> usersIdList = storyViewsList.stream().map(StoryView::getUserId).collect(Collectors.toList());
		Map<String, PublicUserInfo> usersPublicUserInfoMap = cacheManager.getUsersInfoReport(authorization,
				usersIdList);

		List<PublicUserInfo> publicUserInfoList = usersPublicUserInfoMap.values().stream().collect(Collectors.toList());

		return publicUserInfoRespondMapper.toTarget(new PageImpl<PublicUserInfo>(publicUserInfoList));
	}

	private void populateSeenBy(String authorization, List<Story> storiesList) {
		if(CollectionUtils.isEmpty(storiesList)) {
			return;
		}
		List<String> storiesIdList = storiesList.stream().map(Story::getId).collect(Collectors.toList());
		List<StoryView> storyViewList = storyViewService.findUserIdByStoryId(storiesIdList);
		if(CollectionUtils.isEmpty(storyViewList)) {
			return;
		}

		// Story <-> UsersId
		Map<String, List<String>> storyViewByUserIdMap = storyViewList.stream()
				.collect(Collectors.groupingBy(storyView -> storyView.getStoryId(),
						Collectors.mapping(storyView -> storyView.getUserId(), Collectors.toList())));
		List<String> storyUserIdsList = storyViewByUserIdMap.values().stream().flatMap(List::stream)
				.collect(Collectors.toList());

		
		// User <-> PublicUserInfo
		Map<String, PublicUserInfo> usersMap = cacheManager.getUsersInfoReport(authorization, storyUserIdsList);
		if (usersMap != null) {
			// Story <-> (List Of PublicUserInfo)
			Map<String, List<PublicUserInfo>> storyPublicUserInfoMap = storyViewByUserIdMap.entrySet().stream()
					.collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue().stream()
							.map(userId -> usersMap.get(userId)).collect(Collectors.toList())));

			storiesList.stream().forEach(story -> story.setSeenBy(storyPublicUserInfoMap.get(story.getId())));
		}
	}

	private void populateNumberOfUsersSeenStories(String authorization, List<Story> storiesList) {
		if (CollectionUtils.isEmpty(storiesList)) {
			return;
		}
		List<String> storiesIdList = storiesList.stream().map(Story::getId).collect(Collectors.toList());

		List<StoryViewsCount> storyViewsCountList = storyViewService.countByStoryId(storiesIdList);
		if (CollectionUtils.isEmpty(storyViewsCountList)) {
			return;
		}

		// Story <-> seen count
		Map<String, Integer> storyViewByUserIdMap = storyViewsCountList.stream()
				.collect(Collectors.toMap(StoryViewsCount::getStoryId, StoryViewsCount::getCount));

		if (storyViewByUserIdMap != null) {
			storiesList.stream()
					.forEach(story -> story.setUsersSeenNumber(storyViewByUserIdMap.get(story.getId())));
		}
	}

    /**
     * Fetch used hashtags or create them
     *
     * @param story the story to be added, so we can populate its hashtags
     */
    private void populateHashtags(Story story) {
        if (story != null && story.getHashtags() != null) {
            List<Hashtag> tags = story.getHashtags().stream().map(hashtag -> hashtagService.getOrCreate(hashtag)).collect(Collectors.toList());
            story.setHashtags(tags);
        }
    }

    /**
     * Remove story, check if story exists and belong to logged in user, then remove it from story and store_views
     *
     * @param authorization logged in auth code
     * @param storyId story id to be removed
     */
    public void removeStory(String authorization, String storyId) {

        UserInfo userInfo = helper.getCurrentUserInfo();
        String userId = userInfo.getId();
        Optional<Story> story = storyRepository.findById(storyId);
        if (story.isPresent()) {
            if (story.get().getUserId().equals(userId)) {
                try {
                    storyViewRepository.deleteByStoryId(storyId);
                    storyRepository.deleteById(storyId);
                } catch (Exception e) {
                    log.error("Failed to remove story {}", storyId);
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "REMOVE_STORY_FAILED");
                }
            } else {
                log.error("Cant remove story {}", storyId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "CAN_NOT_REMOVE_STORY");
            }

        } else {
            log.error("story Not Found {}", storyId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND");
        }
    }

    /**
     * Retrieve story seen by usera list
     *
     * @param authorization logged in auth code
     * @param storyId       to retrieve its seen by users
     * @param page          number
     * @param size          of page
     * @return GetStorySeenByUsersRespond, a page of users
     */
    @Override
    public GetStorySeenByUsersRespond getStorySeenByUsers(String authorization, String storyId, Integer page, Integer size) {
        if (!ObjectId.isValid(storyId)) {
            log.error("StoryId is invalid {}",  storyId);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STORY_ID_INVALID");
        }

        UserInfo userInfo = helper.getCurrentUserInfo();
        Optional<Story> story = storyRepository.findById(storyId);
        if (!story.isPresent()) {
            log.error("story Not Found {}", storyId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND");
        }

        if (!story.get().getUserId().equals(userInfo.getId())){
            log.error("Can't get story seen by, forbidden {}", storyId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "GET_STORY_SEEN_BY_FORBIDDEN");
        }

        /**
         * Retrieve logged in user followings (Followed by me
         */
        CompletableFuture<List<String>> followingsIdsFuture = followService.getUserFollowingsIdsAsync(authorization, page, size);
        List<String> followingsIds = new ArrayList<>();
        try {
            followingsIds = followingsIdsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Thread.currentThread().interrupt();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "FETCH_FOLLOWING_USERS_FAILED");
        }

        Pageable pageable = helper.pageable(page, size, Sort.by("createdDt").ascending());
        Page<StoryView> storyViews = storyViewService.findByStoryId(storyId, pageable);
        List<String> userIds = storyViews.map(StoryView::getUserId).stream().collect(Collectors.toList());
       
        Map<String, PublicUserInfo> userInfoMap = cacheManager.getUsersInfoReport(authorization, userIds);
        /*
         * Iterate over likes page to make sure user info added to the list in the same order they retrieved from Like table
         */
        List<PublicUserInfo> list = new ArrayList<>();
        userIds.forEach(userId -> list.add(userInfoMap.get(userId)));

        List<String> finalFollowingsIds = followingsIds;
        List<CustomPublicUserInfo> newList = list.stream().map(publicUserInfo -> {
            CustomPublicUserInfo customPublicUserInfo =  new CustomPublicUserInfo();
            customPublicUserInfo.setFollowedByMe(finalFollowingsIds.contains(publicUserInfo.getUserId()));
            customPublicUserInfo.setProfilePictureUrl(publicUserInfo.getProfilePictureUrl());
            customPublicUserInfo.setUserName(publicUserInfo.getUserName());
            customPublicUserInfo.setUserId(publicUserInfo.getUserId());
            return customPublicUserInfo;
        }).collect(Collectors.toList());

        GetStorySeenByUsersRespond getStorySeenByUsersRespond = new GetStorySeenByUsersRespond();
        getStorySeenByUsersRespond.setStoryId(storyId);
        getStorySeenByUsersRespond.setData(customPublicUserInfoMapper.toTargets(newList));
        getStorySeenByUsersRespond.setPageNumber(storyViews.getNumber() + 1);
        getStorySeenByUsersRespond.setPageSize(storyViews.getSize());
        getStorySeenByUsersRespond.setTotalElements(storyViews.getTotalElements());
        getStorySeenByUsersRespond.setTotalPages(storyViews.getTotalPages());
        return getStorySeenByUsersRespond;
    }

    /**
     * Get soey owner id
     *
     * @param authorization logged in user auth code
     * @param storyId       to get its owner
     * @return PostDetails
     */
    @Override
    public StoryOwnerId getStoryOwnerId(String authorization, String storyId) {
        Optional<Story> story = storyRepository.findById(storyId);
        if(story.isPresent()) {
            return new StoryOwnerId().userId(story.get().getUserId());
        }
        return new StoryOwnerId().userId(null);
    }
}
