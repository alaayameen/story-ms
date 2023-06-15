package com.social.story.services;

import com.social.swagger.model.story.AddStoryRequest;
import com.social.swagger.model.story.GetMyStoriesRespond;
import com.social.swagger.model.story.GetStorySeenByUsersRespond;
import com.social.swagger.model.story.PublicUserInfoRespond;
import com.social.swagger.model.story.StoryOwnerId;

/**
 * @author ayameen
 *
 */
public interface MyStoriesService {

    /**
     * Add new user story
     *
     * @param authorization
     * @param userId user who adding this story
     * @param addStoryRequest story details to be added
     * @return Story Id
     */
    String addStory(String authorization, String userId, AddStoryRequest addStoryRequest);

    /**
     * Retrieve my stories, pageable
     *
     * @param userId logged in user
     * @param page page number to get
     * @param size page size
     * @return page of logged in user stories
     */
    GetMyStoriesRespond getMyStories(String authorization, String userId, Integer page, Integer size);

    /**
     * Retrieve user's stories, with pageable
     *
     * @param userId logged in user
     * @param page page number to get
     * @param size page size
     * @return page of logged in user stories
     */
    GetMyStoriesRespond getUserStories(String authorization, String userId, Integer page, Integer size);

    PublicUserInfoRespond getSeenUsersByStoryId(String authorization, String storyId, Integer page, Integer size);

    /**
     * remove story
     *
     * @param authorization logged in auth code
     * @param storyId story id to be removed
     */
    void removeStory(String authorization, String storyId);

    /**
     * Retrieve story seen users list
     *
     * @param authorization logged in auth code
     * @param storyId to retrieve its seen by users
     * @param page number
     * @param size of page
     * @return GetStorySeenByUsersRespond, a page of users
     */
    GetStorySeenByUsersRespond getStorySeenByUsers(String authorization, String storyId, Integer page, Integer size);

    /**
     * Get soey owner id
     *
     * @param authorization logged in user auth code
     * @param storyId to get its owner
     * @return PostDetails
     */
    public StoryOwnerId getStoryOwnerId(String authorization, String storyId);

}

