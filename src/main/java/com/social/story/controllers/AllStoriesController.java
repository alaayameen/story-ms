package com.social.story.controllers;

import javax.validation.constraints.NotNull;

import com.social.story.services.AllStoriesService;
import com.social.story.services.ReactionsService;
import com.social.story.utils.StoryHelper;
import org.springframework.stereotype.Controller;

import com.social.core.models.UserInfo;
import com.social.story.data.mongo.models.ReactionType;
import com.social.swagger.model.story.DeleteContentByReportRequest;
import com.social.swagger.model.story.DeleteStoriesByUserIdsRequest;
import com.social.swagger.model.story.DeleteStoriesByUserIdsRespond;
import com.social.swagger.model.story.GetFollowingStoriesRespond;
import com.social.swagger.model.story.ReactionDetailsPayload;
import com.social.swagger.model.story.ReactionDetailsPayload.ReactionTypeEnum;
import com.social.swagger.model.story.RetrieveFollowingsStoriesRespond;
import com.social.swagger.model.story.StoryDetails;
import com.social.swagger.model.story.StoryReactionsResponse;
import com.social.swagger.model.story.UserStatus;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;


/**
 * @author ayameen
 *
 * */
@Log4j2
@Controller
@AllArgsConstructor
public class AllStoriesController {

    AllStoriesService allStoriesService;

    StoryHelper helper;

    private ReactionsService reactionsService;
    /**
     * Mark a story as seen
     *
     * @param authorization logged in user auth code
     * @param storyId       story id to be marked as seen by logged in user
     */
    public void markStoryAsViewed(String authorization, String storyId) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to mark story {}, as seen by User Id {}", storyId, userInfo.getId());
        allStoriesService.markStoryAsViewed(authorization, storyId);
        log.debug("Story: story Id {} is successfully marked as seen by User Id: {}", storyId, userInfo.getId());
    }

    /**
     * Return page of stories for the provided following user added during the last 24 hours.
     *
     * @param authorization logged in user auth code
     * @param userId        to fetch it's stories
     * @param page          page number
     * @param size          page size
     * @return page of @userId stories
     */
    public GetFollowingStoriesRespond getFollowingStories(String authorization, String userId, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to retrieve following user {} stories, by user id: {}", userId, userInfo.getId());
        GetFollowingStoriesRespond getFollowingStoriesRespond = allStoriesService.getFollowingStories(authorization, userId, page, size);
        log.debug("Following user {} stories is successfully retrieved, by user id: {}", userId, userInfo.getId());
        return getFollowingStoriesRespond;

    }

    /**
     * Return page of stories for following users added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
     *
     * @param authorization logged in user auth code
     * @param page          page number
     * @param size          page size
     * @return page of following users stories
     */
    public RetrieveFollowingsStoriesRespond getFollowingsStories(String authorization, Integer page, Integer size) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to retrieve following users stories by user id {}", userInfo.getId());
        RetrieveFollowingsStoriesRespond retrieveFollowingsStoriesRespond = allStoriesService.getFollowingsStories(authorization, page, size);
        log.debug("Following users stories is successfully retrieved by user id {}", userInfo.getId());
        return retrieveFollowingsStoriesRespond;
    }

    /**
     * Delete users stories by scheduler-ms, for admin use only
     *
     * @param authorization           admin auth token
     * @param deleteUsersByIdsRequest request body contains a list of user ids
     * @return DeleteStoriesByUserIdsRespond, user id with deletion status
     */
    public DeleteStoriesByUserIdsRespond deleteStoriesByUserIds(String authorization, DeleteStoriesByUserIdsRequest deleteUsersByIdsRequest) {
        log.debug("A request to delete stories for user ids {}", deleteUsersByIdsRequest.getUsersIds());
        DeleteStoriesByUserIdsRespond retrieveFollowingsStoriesRespond = allStoriesService.deleteStoriesByUserIds(deleteUsersByIdsRequest);
        log.debug("Users stories is successfully deleted for user ids {}", deleteUsersByIdsRequest.getUsersIds());
        return retrieveFollowingsStoriesRespond;
    }

    /**
     * Soft delete story based on approved report
     *
     * @param authorization logged in user auth token
     * @param storyId to be deleted
     * @param deleteContentByReportRequest request body
     */
    public void softDeleteByReportContent(String authorization, String storyId, DeleteContentByReportRequest deleteContentByReportRequest) {
        log.debug("A request to delete story {} due to report {}", storyId, deleteContentByReportRequest.getReportId());
        allStoriesService.softDeleteByReportContent(authorization, storyId, deleteContentByReportRequest);
        log.debug("Story {} is successfully deleted due to report {}", storyId, deleteContentByReportRequest.getReportId());
    }

    /**
     * Restore deleted content by report
     *
     * @param authorization logged in user auth token
     * @param contentId to be deleted
     */
    public void restoreDeletedContentByReport(String authorization, String contentId) {
        log.debug("A request to restore deleted story {} by report", contentId);
        allStoriesService.restoreDeletedContentByReport(authorization, contentId);
        log.debug("Story {} is successfully restored", contentId);
    }

    /**
     * Hard delete content by admin
     *
     * @param authorization logged in user auth token
     * @param contentId to be deleted
     */
    public void hardDeleteByAdmin(String authorization, String contentId)	{
        log.debug("A request to hard deleted story {} by report", contentId);
        allStoriesService.hardDeleteByAdmin(authorization, contentId);
        log.debug("Story {} is successfully hard deleted", contentId);
    }

    /**
     * Invert the user activation status for all owned content, so we can hide any content owned by deactivated user
     *
     * @param authorization logged in user auth token
     * @param userId to be mark as deactivated based on owner user status
     * @param status of owner user
     */
    public void invertUserStatus(String authorization, String userId, UserStatus status)	{
        log.debug("A request to invertUserStatus user {}", userId);
        allStoriesService.invertUserStatus(status, userId);
        log.debug("invertUserStatus is successfully called for user {} ", userId);
    }

    /**
     * Invert the user content as marked for delete based on delete account process
     *
     * @param authorization logged in user auth token
     * @param userId to be mark as deactivated based on owner user status
     * @param status of owner user
     */
    public void softDeleteRestoreContentByDeleteAccount(String authorization, String userId, UserStatus status)	{
        log.debug("A request to softDeleteRestoreContentByDeleteAccount user {}", userId);
        allStoriesService.softDeleteRestoreContentByDeleteAccount(status, userId);
        log.debug("softDeleteRestoreContentByDeleteAccount is successfully called for user {} ", userId);
    }
    
    /**
	 * Get story by Id.
	 *
	 * @param storyId for story to get.
	 */
	public StoryDetails getStoryById(@NotNull String storyId) {
		helper.verifyAdminUserPrevilidge();
		log.debug("A request to get story by Id {}", storyId);
		return allStoriesService.getStoryById(storyId);
	}

	/**
	 * React on given story.
	 *
	 * @param authorization          Authorization object
	 * @param storyId                The storyId to add reaction on it
	 * @param reactionDetailsPayload The payload that contains the reaction type
	 */
	public void saveStoryReaction(String authorization, String storyId, ReactionDetailsPayload reactionDetailsPayload) {
		UserInfo userInfo = helper.getCurrentUserInfo();
		String userId = userInfo.getId();
		log.debug("A request to react on a story {}", storyId, userInfo.getId());

		// Set default value for ReactionType = LIKE
		if (reactionDetailsPayload == null) {
			reactionDetailsPayload = new ReactionDetailsPayload();
			reactionDetailsPayload.setReactionType(ReactionTypeEnum.LIKE);
		}
		reactionsService.saveStoryReaction(userId, storyId,
				ReactionType.valueOf(reactionDetailsPayload.getReactionType().toString()));
		log.debug("Story: Reaction on story Id {} is successfully completed userId: {}", storyId, userInfo.getId());
	}

	public StoryReactionsResponse getStoryReactions(String authorization, String storyId, Integer page, Integer size) {
		UserInfo userInfo = helper.getCurrentUserInfo();
		String userId = userInfo.getId();
		log.debug("A request to retrieve story {} reactions", storyId);
		StoryReactionsResponse storyReactionsResponse = reactionsService.getStoryReactions(storyId, page, size);
		log.debug("Following user {} stories is successfully retrieved, by user id: {}", userId, userInfo.getId());
		return storyReactionsResponse;
	}
}