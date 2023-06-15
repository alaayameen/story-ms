package com.social.story.controllers;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.database.annotations.NotNull;
import com.social.core.models.UserInfo;
import com.social.story.services.MyStoriesService;
import com.social.story.services.impl.FileUploadService;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.story.AddStoryRequest;
import com.social.swagger.model.story.AddStoryResponse;
import com.social.swagger.model.story.GetMyStoriesRespond;
import com.social.swagger.model.story.GetStorySeenByUsersRespond;
import com.social.swagger.model.story.PublicUserInfoRespond;
import com.social.swagger.model.story.StoryOwnerId;
import com.social.swagger.model.story.UploadUrlResponse;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;


/**
 * @author ayameen
 *
 * */
@Log4j2
@Controller
@AllArgsConstructor
public class MyStoriesController {

    MyStoriesService myStoriesService;
    
    FileUploadService fileUploadService;

    StoryHelper helper;

    /**
     * Add new story
     *
     * @param addStoryRequest story details
     * @return story id
     */
    public AddStoryResponse addStory(String authorization, AddStoryRequest addStoryRequest) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to add story, User Id {}", userInfo.getId());
        String storyId = myStoriesService.addStory(authorization, userInfo.getId(), addStoryRequest);
        log.debug("Story: story Id {} is successfully added", storyId);
        return new AddStoryResponse().storyId(storyId);
    }

    /**
     * Retrieve my stories, pageable
     *
     * @param authorization logged in auth code
     * @param page          page  to get
     * @param size          size of page
     * @return page of logged in user stories
     */
    public GetMyStoriesRespond getMyStories(String authorization, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to add get my stories, User Id {}", userInfo.getId());
        GetMyStoriesRespond getMyStoriesRespond = myStoriesService.getMyStories(authorization, userInfo.getId(), page, size);
        log.debug("My stories is successfully retrieved: user Id {}", userInfo.getId());
        return getMyStoriesRespond;
    }

    /**
     * Retrieve user's stories, with pagination
     *
     * @param authorization logged in auth code
     * @param page          page  to get
     * @param size          size of page
     * @return page of logged in user stories
     */
    public GetMyStoriesRespond getUserStories(String authorization, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to add get user's stories, User Id {}", userInfo.getId());
        GetMyStoriesRespond getMyStoriesRespond = myStoriesService.getUserStories(authorization, userInfo.getId(), page, size);
        log.debug("User stories successfully retrieved: user Id {}", userInfo.getId());
        return getMyStoriesRespond;
    }

    /**
     * Retrieve story seen users list, with pagination
     *
     * @param authorization logged in auth code
     * @param page          page  to get
     * @param size          size of page
     * @return page of logged in user stories
     */
    public PublicUserInfoRespond getSeenUsersByStoryId(String authorization, String storyId, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to get story: {} seen by users, User Id {}", storyId,  userInfo.getId());
        PublicUserInfoRespond publicUserInfoRespond = myStoriesService.getSeenUsersByStoryId(authorization, storyId, page, size);
        log.debug("Story:{} seen by users are successfully retrieved: user Id {}", storyId, userInfo.getId());
        return publicUserInfoRespond;
    }

    /**
     * remove story
     *
     * @param authorization logged in auth code
     * @param storyId       story id to be removed
     */
    public void removeStory(String authorization, String storyId) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to remove story {} by User Id {}", storyId, userInfo.getId());
        myStoriesService.removeStory(authorization, storyId);
        log.debug("Story: story Id {} is successfully removed by User Id: {}", storyId, userInfo.getId());
    }

    /**
     * Retrieve story seen by users list
     *
     * @param authorization logged in auth code
     * @param storyId       to retrieve its seen by users
     * @param page          number
     * @param size          of page
     * @return GetStorySeenByUsersRespond, a page of users
     */
    public GetStorySeenByUsersRespond getStorySeenByUsers(String authorization, String storyId, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to get story: {} seen by users, User Id {}", storyId,  userInfo.getId());
        GetStorySeenByUsersRespond getStorySeenByUsersRespond = myStoriesService.getStorySeenByUsers(authorization, storyId, page, size);
        log.debug("Story:{} seen by users are successfully retrieved: user Id {}", storyId, userInfo.getId());
        return getStorySeenByUsersRespond;
    }

    /**
     * Get soey owner id
     *
     * @param authorization logged in user auth code
     * @param storyId       to get its owner
     * @return PostDetails
     */
    public StoryOwnerId getStoryOwnerId(String authorization, String storyId) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to get story: {} owner id, User Id {}", storyId, userInfo.getId());
        StoryOwnerId storyOwnerId = myStoriesService.getStoryOwnerId(authorization, storyId);
        log.debug("Story {} owner id is successfully retrieved, User Id {}", storyId, userInfo.getId());
        return storyOwnerId;
    }
    
    public UploadUrlResponse getUploadUrl(String authorization, @NotNull @Valid String fileKey) {

		String signedUrl;
		try {
			signedUrl = fileUploadService.generateSignedUrl(fileKey);
		} catch (Exception e) {
			log.error("Failed to generate signed url");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "SIGNED_URL_GENERATION_FAILED");
		}
		UploadUrlResponse response = new UploadUrlResponse();
		response.setUrl(signedUrl);
		return response;
	}
}