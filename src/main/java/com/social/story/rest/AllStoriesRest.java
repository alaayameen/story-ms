package com.social.story.rest;

import com.social.story.controllers.AllStoriesController;
import com.social.swagger.api.story.AllStoriesApi;
import com.social.swagger.model.story.*;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ayameen
 */
@RestController
@AllArgsConstructor
public class AllStoriesRest implements AllStoriesApi {

    AllStoriesController allStoriesController;

    @Override
    public ResponseEntity<DeleteStoriesByUserIdsRespond> deleteStoriesByUserIds(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                                                @ApiParam(value = ""  )  @Valid @RequestBody DeleteStoriesByUserIdsRequest deleteUsersByIdsRequest) {
        return ResponseEntity.ok(allStoriesController.deleteStoriesByUserIds(authorization, deleteUsersByIdsRequest ));
    }

    @Override
    public ResponseEntity<GetFollowingStoriesRespond> getFollowingStories(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                                          @ApiParam(value = "",required=true) @PathVariable("userId") String userId,
                                                                          @ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") Integer page,
                                                                          @ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") Integer size) {
        return ResponseEntity.ok(allStoriesController.getFollowingStories(authorization, userId, page, size));
    }

    @Override
    public ResponseEntity<Void> markStoryAsViewed(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                  @ApiParam(value = "storyId",required=true) @PathVariable("storyId") String storyId) {
        allStoriesController.markStoryAsViewed(authorization, storyId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RetrieveFollowingsStoriesRespond> retrieveFollowingsStories(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                                                      @ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") Integer page,
                                                                                      @ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") Integer size) {
        return ResponseEntity.ok(allStoriesController.getFollowingsStories(authorization, page, size));
    }

    @Override
    public ResponseEntity<Void> softDeleteByReportContent(String authorization, String storyId, @Valid DeleteContentByReportRequest deleteContentByReportRequest) {
        allStoriesController.softDeleteByReportContent(authorization, storyId, deleteContentByReportRequest);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> softDeleteRestoreContentByDeleteAccount(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                                        @ApiParam(value = "the userId.",required=true) @PathVariable("userId") String userId,
                                                                        @ApiParam(value = "" ,required=true )  @Valid @RequestBody UserStatus status) {
        allStoriesController.softDeleteRestoreContentByDeleteAccount(authorization, userId, status);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> restoreDeletedContentByReport(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                              @ApiParam(value = "the contentId.",required=true) @PathVariable("contentId") String contentId) {
        allStoriesController.restoreDeletedContentByReport(authorization, contentId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> hardDeleteByAdmin(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                  @ApiParam(value = "the contentId.",required=true) @PathVariable("contentId") String contentId) {
        allStoriesController.hardDeleteByAdmin(authorization, contentId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> invertUserStatus(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                 @ApiParam(value = "the userId.",required=true) @PathVariable("userId") String userId,
                                                 @ApiParam(value = "" ,required=true )  @Valid @RequestBody UserStatus status) {
        allStoriesController.invertUserStatus(authorization, userId, status);
        return ResponseEntity.noContent().build();
    }

	@Override
	public ResponseEntity<StoryDetails> getStoryById(
			@ApiParam(value = "") @RequestHeader(value = "Authorization", required = false) String authorization,
			@ApiParam(value = "storyId of story to get.", required = true) @PathVariable("storyId") String storyId){

		return ResponseEntity.ok(allStoriesController.getStoryById(storyId));
	}

	public ResponseEntity<Void> saveStoryReaction(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "The storyId.", required = true) @PathVariable("storyId") String storyId,
			@ApiParam(value = "") @Valid @RequestBody ReactionDetailsPayload reactionDetailsPayload) {
		allStoriesController.saveStoryReaction(authorization, storyId, reactionDetailsPayload);
		return ResponseEntity.noContent().build();
	}

	public     ResponseEntity<StoryReactionsResponse> getStoryReactions(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization, @ApiParam(value = "",required=true) @PathVariable("storyId") String storyId, @ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") Integer page, @ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") Integer size){
		return ResponseEntity.ok(allStoriesController.getStoryReactions(authorization, storyId, page, size));

	}

}
