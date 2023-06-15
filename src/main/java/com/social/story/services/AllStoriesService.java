package com.social.story.services;

import javax.validation.constraints.NotNull;

import com.social.swagger.model.story.*;

/**
 * @author ayameen
 *
 */
public interface AllStoriesService {
    /**
     * Mark a story as seen
     *
     * @param authorization logged in auth code
     * @param storyId story id to be marked as seen by logged in user
     */
    void markStoryAsViewed(String authorization, String storyId);

    /**
     * Return page of stories for the provided following user added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
     *
     * @param authorization logged in user auth code
     * @param userId to fetch it's stories
     * @param page page number
     * @param size page size
     * @return page of @userId stories
     */
    GetFollowingStoriesRespond getFollowingStories(String authorization, String userId, Integer page, Integer size);

    /**
     * Return page of stories for following users added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
     *
     * @param authorization logged in user auth code
     * @param page page number
     * @param size page size
     * @return page of following users stories
     */
    RetrieveFollowingsStoriesRespond getFollowingsStories(String authorization, Integer page, Integer size);

    /**
     * Delete users stories by scheduler-ms, for admin use only
     *
     * @param deleteUsersByIdsRequest request body contains a list of user ids
     * @return DeleteStoriesByUserIdsRespond, user id with deletion status
     */
    DeleteStoriesByUserIdsRespond deleteStoriesByUserIds(DeleteStoriesByUserIdsRequest deleteUsersByIdsRequest);

    /**
     * Soft delete story based on approved report
     *
     * @param authorization logged in user auth token
     * @param storyId to be deleted
     * @param deleteContentByReportRequest request body
     */
    void softDeleteByReportContent(String authorization, String storyId, DeleteContentByReportRequest deleteContentByReportRequest);

    /**
     * Restore deleted content by report
     *
     * @param authorization logged in user auth token
     * @param contentId to be deleted
     */
    void restoreDeletedContentByReport(String authorization, String contentId);

    /**
     * Hard delete content by admin
     *
     * @param authorization logged in user auth token
     * @param contentId to be deleted
     */
    void hardDeleteByAdmin(String authorization, String contentId);

    /**
     * Invert the user activation status for all owned content, so we can hide any content owned by deactivated user
     *
     * @param status active or not
     * @param userId to be mark as deactivated based on owner user status
     */
    void invertUserStatus(UserStatus status, String userId);

    /**
     * Invert the user content as marked for delete based on delete account process
     *
     * @param userId to be mark as deactivated based on owner user status
     * @param status of owner user
     */
    void softDeleteRestoreContentByDeleteAccount(UserStatus status, String userId);


    /**
     * Get story by Id
     *
     * @param storyId to get the story
     * @return StoryDetails
     */
    StoryDetails getStoryById(@NotNull String storyId);

}
