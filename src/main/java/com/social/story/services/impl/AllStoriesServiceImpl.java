package com.social.story.services.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.validation.constraints.NotNull;

import com.social.story.data.mongo.dao.StoryRepository;
import com.social.story.data.mongo.dao.impl.StoryViewRepositoryImpl;
import com.social.story.mappers.StoryDetailsMapper;
import com.social.story.mappers.UserStoriesMapper;
import com.social.story.services.StoryViewService;
import com.social.story.utils.StoryHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ResponseStatusException;

import com.social.core.models.UserInfo;
import com.social.story.data.mongo.dao.StoryViewRepository;
import com.social.story.data.mongo.models.Story;
import com.social.story.data.mongo.models.StoryView;
import com.social.story.data.mongo.models.UserStories;
import com.social.story.gateway.FollowApiClient;
import com.social.story.gateway.UsersApiClient;
import com.social.story.mappers.GetFollowingStoriesRespondMapper;
import com.social.story.services.AllStoriesService;
import com.social.story.utils.CacheManager;
import com.social.swagger.called.follow.model.GetFollowingStatusRequest;
import com.social.swagger.called.follow.model.GetFollowingStatusRespond;
import com.social.swagger.called.user.model.PublicUserInfo;
import com.social.swagger.model.story.DeleteContentByReportRequest;
import com.social.swagger.model.story.DeleteStoriesByUserIdsRequest;
import com.social.swagger.model.story.DeleteStoriesByUserIdsRespond;
import com.social.swagger.model.story.DeleteUsersStatus;
import com.social.swagger.model.story.GetFollowingStoriesRespond;
import com.social.swagger.model.story.RetrieveFollowingsStoriesRespond;
import com.social.swagger.model.story.StoryDetails;
import com.social.swagger.model.story.UserStatus;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author ayameen
 *
 */
@Log4j2
@Service
@AllArgsConstructor
public class AllStoriesServiceImpl implements AllStoriesService {

    StoryViewRepository storyViewRepository;

    StoryHelper helper;

    StoryRepository storyRepository;

    FollowApiClient followApiClient;

    UsersApiClient usersApiClient;

    GetFollowingStoriesRespondMapper getFollowingStoriesRespondMapper;

    UserStoriesMapper userStoriesMapper;

    StoryViewService storyViewService;

    StoryViewRepositoryImpl storyViewRepositoryImpl;

    MongoTemplate mongoTemplate;

    StoryDetailsMapper storyDetailsMapper;
    
    CacheManager cacheManager;

	/**
	 * Mark a story as seen, check if story exists and not already viewed by the
	 * user, then mark it as seen
	 *
	 * @param authorization logged in auth code
	 * @param storyId       story id to be marked as seen by logged in user
	 */
	public void markStoryAsViewed(String authorization, String storyId) {

		UserInfo userInfo = helper.getCurrentUserInfo();
		String userId = userInfo.getId();

		Optional<Story> storyObject = storyRepository.findById(storyId);
		if (!storyObject.isPresent()) {
			log.error("story Not Found {}", storyId);
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND");
		}
		try {
			// Make sure userId with storyId together are unique
			Optional<StoryView> storyViewObj = storyViewRepository.findByStoryIdAndUserId(storyId, userId);
			if (storyViewObj.isPresent()) {
				log.info("story with id {} is already viewed by user {} ", storyId, userId);
				return;
			}
			// Save the new StoryView object
			StoryView newStoryView = new StoryView();
			newStoryView.setStoryId(storyId);
			newStoryView.setUserId(userId);
			storyViewRepository.save(newStoryView);

			// Update number of users seen this story
			Story story = storyObject.get();
			if (story.getSeenByIds() != null && !story.getSeenByIds().contains(userId)) {
				story.getSeenByIds().add(userId);
			}
			story.setUsersSeenNumber(story.getUsersSeenNumber() + 1);
			storyRepository.save(story);
		} catch (Exception e) {
			log.error("Failed to mark story as viewed {}", storyId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MARK_STORY_AS_VIEWED_FAILED");
		}
	}

    /**
     * Return page of stories for the provided following user added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
     *
     * @param authorization logged in user auth code
     * @param followingUserId to fetch it's stories
     * @param page page number
     * @param size page size
     * @return page of @userId stories
     */
    @Override
    public GetFollowingStoriesRespond getFollowingStories(String authorization, String followingUserId, Integer page, Integer size) {

        UserInfo userInfo = helper.getCurrentUserInfo();
        List<String> usersList = new ArrayList<>();
        usersList.add(followingUserId);

        /*
         * Retrieve logged in user following user by followingUserId (using follow-ms api gateway),
         */
        Map<String, PublicUserInfo> usersMap = cacheManager.getUsersInfoReport(authorization, usersList);
        if (usersMap != null && !CollectionUtils.isEmpty(usersMap.values()) ) {
        	GetFollowingStatusRequest getFollowingStatusRequest = new GetFollowingStatusRequest();
        	getFollowingStatusRequest.addUsersIdsItem(followingUserId);
        	GetFollowingStatusRespond followingStatusRespond = followApiClient.getFollowingStatus(authorization, getFollowingStatusRequest);
        	if (followingStatusRespond == null || !followingStatusRespond.get(0).isFollowing()) {
                log.error("Failed to get stories of user id {}", followingUserId);
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "GET_FOLLOWING_STORIES_FORBIDDEN");
            }
        } else {
            log.error("following User Not Found {}", followingUserId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FOLLOWING_USER_NOT_FOUND");
        }

        /*
         * Create pageable object, return not seen stories first with indication for not seen sorted by created date LIFO
         * and map the result to GetFollowingStoriesRespond
         */
		Pageable pageable = helper.pageable(page, size,
				Sort.by("usersSeenNumber").ascending().and(Sort.by("createdDt").descending()));
		Page<Story> storiesList = storyViewRepositoryImpl.followingStories(usersList, userInfo.getId(), pageable);
		storiesList.stream().forEach(story -> story.setPublicUserInfo(usersMap.get(story.getUserId())));

        return getFollowingStoriesRespondMapper.toTarget(storiesList);
    }

    /**
     * Return page of stories for following users added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
     *
     * @param authorization logged in user auth code
     * @param page page number
     * @param size page size
     * @return page of following users stories
     */
    @Override
    public RetrieveFollowingsStoriesRespond getFollowingsStories(String authorization, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();

        /*
          Retrieve logged in user following users (using follow-ms api gateway),
          Put them in a Map to ease getting the PublicUserInfo by it's Id in populateSeenAndPublicUserInfo method below.
         */
        List<String> followingsIds = followApiClient.getUserFollowingsIds(authorization, page, size);
        Map<String, PublicUserInfo> followingsDetails = cacheManager.getUsersInfoReport(authorization, followingsIds);
        /*
         * Create pageable object, return not seen stories first with indication for not seen sorted by created date LIFO
         */
        Pageable pageable = helper.pageable(page, size, Sort.by("seen").ascending().and(Sort.by("createdDt").descending()));
        Page<UserStories> stories  = storyViewRepositoryImpl.followingStoriesGroupByUser(followingsIds, userInfo.getId(), pageable);
        stories.map(item -> populateSeenAndPublicUserInfo(followingsDetails.get(item.getId()), item));

        /*
         * Mapping stories to the GetFollowingStoriesRespond
         */
        RetrieveFollowingsStoriesRespond retrieveFollowingsStoriesRespond =  new RetrieveFollowingsStoriesRespond();
        retrieveFollowingsStoriesRespond
                .data(userStoriesMapper.toTargets(stories.getContent()))
                .pageSize(stories.getSize())
                .pageNumber(stories.getNumber() + 1)
                .totalElements(stories.getTotalElements())
                .totalPages(stories.getTotalPages());

        return retrieveFollowingsStoriesRespond;
    }

    /**
     * Delete users stories by scheduler-ms, for admin use only
     *
     * @param deleteUsersByIdsRequest request body contains a list of user ids
     * @return DeleteStoriesByUserIdsRespond, user id with deletion status
     */
    @Override
    public DeleteStoriesByUserIdsRespond deleteStoriesByUserIds(DeleteStoriesByUserIdsRequest deleteUsersByIdsRequest) {
        DeleteStoriesByUserIdsRespond deleteStoriesByUserIdsRespond =  new DeleteStoriesByUserIdsRespond();
        deleteUsersByIdsRequest.getUsersIds().forEach(userId -> {
            DeleteUsersStatus deleteUsersStatus =  new DeleteUsersStatus();
            deleteUsersStatus.setUserId(userId);
            try {
                List<Story> stories = storyRepository.findByUserId(userId);
                stories.forEach(story -> storyViewRepository.deleteByStoryId(story.getId()));
                storyRepository.deleteByUserId(userId);
                deleteUsersStatus.setDeleteStatus(Boolean.TRUE);

            } catch (Exception e) {
                log.error("Failed to delete user: {} stories", userId);
                deleteUsersStatus.setDeleteStatus(Boolean.FALSE);
            }
            deleteStoriesByUserIdsRespond.add(deleteUsersStatus);
        });

        return deleteStoriesByUserIdsRespond;
    }

    /**
     * Soft delete story based on approved report
     *
     * @param authorization                logged in user auth token
     * @param storyId                      to be deleted
     * @param deleteContentByReportRequest request body
     */
    @Override
    public void softDeleteByReportContent(String authorization, String storyId, DeleteContentByReportRequest deleteContentByReportRequest) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        Optional<Story> story = storyRepository.findById(storyId);
        if (!story.isPresent()) {
            log.error("story Not Found {}", storyId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND");
        }

        Story storyValues = story.get();
        storyValues.setReportId(deleteContentByReportRequest.getReportId());
        storyValues.setReportDeletedBy(userInfo.getId());
        storyValues.setIsDeletedByReport(Boolean.TRUE);
        storyValues.setReportDeletedDt(LocalDateTime.now());
        storyRepository.save(storyValues);
    }

    /**
     * Restore deleted content by report
     *
     * @param authorization logged in user auth token
     * @param contentId     to be deleted
     */
    @Override
    public void restoreDeletedContentByReport(String authorization, String contentId) {
        Optional<Story> entityOptional = storyRepository.findById(contentId);
        if (!entityOptional.isPresent()) {
            log.error("story not found {}", contentId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND");
        }

        Story entityValues = entityOptional.get();
        entityValues.setReportId(null);
        entityValues.setReportDeletedBy(null);
        entityValues.setIsDeletedByReport(null);
        entityValues.setReportDeletedDt(null);
        storyRepository.save(entityValues);
    }

    /**
     * Hard delete content by admin
     *
     * @param authorization logged in user auth token
     * @param contentId to be deleted
     */
    public void hardDeleteByAdmin(String authorization, String contentId)	{
        Optional<Story> entityOptional = storyRepository.findById(contentId);
        if (!entityOptional.isPresent()) {
            log.error("story not found {}", contentId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND");
        }
        storyViewRepository.deleteByStoryId(contentId);
        storyRepository.deleteById(contentId);
    }

    /**
     * Invert the user activation status for all owned content, so we can hide any content owned by deactivated user
     *
     * @param status active or not
     * @param userId to be mark as deactivated based on owner user status
     */
    public void invertUserStatus(UserStatus status, String userId)	{
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        Update update = new Update();
        update.set("isOwnerUserActive", status.isActive());
        mongoTemplate.updateMulti(query, update, Story.class);
    }

    private UserStories populateSeenAndPublicUserInfo(PublicUserInfo publicUserInfo, UserStories userStories) {
        if (userStories != null) {
            userStories.setPublicUserInfo(publicUserInfo);
            userStories.getStories().forEach(story -> story.setPublicUserInfo(publicUserInfo));
        }
        return userStories;
    }

    /**
     * Invert the user content as marked for delete based on delete account process
     *
     * @param userId to be mark as deactivated based on owner user status
     * @param status of owner user
     */
    public void softDeleteRestoreContentByDeleteAccount(UserStatus status, String userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId));
        Update update = new Update();
        update.set("isOwnerAccountMarkedForDelete", !status.isActive());
        mongoTemplate.updateMulti(query, update, Story.class);
    }
    
    @Override
	public StoryDetails getStoryById(@NotNull String storyId) {
        
    	    Story story = storyRepository.findById(storyId)
 				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "STORY_NOT_FOUND"));
	    return storyDetailsMapper.toSource(story);
	}
    
}


