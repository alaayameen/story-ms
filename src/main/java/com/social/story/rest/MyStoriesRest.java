package com.social.story.rest;

import com.social.story.controllers.MyStoriesController;
import com.social.swagger.api.story.MyStoriesApi;
import com.social.swagger.model.story.*;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author ayameen
 */
@RestController
@AllArgsConstructor
public class MyStoriesRest implements MyStoriesApi {

    MyStoriesController myStoriesController;

    @Override
    public ResponseEntity<AddStoryResponse> addStory(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                     @ApiParam(value = ""  )  @Valid @RequestBody AddStoryRequest addStoryRequest) {
        return ResponseEntity.ok(myStoriesController.addStory(authorization, addStoryRequest));
    }

    @Override
    public ResponseEntity<GetMyStoriesRespond> getMyStories(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                            @ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") Integer page,
                                                            @ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") Integer size) {
        return ResponseEntity.ok(myStoriesController.getMyStories(authorization, page, size));
    }

    @Override
    public ResponseEntity<StoryOwnerId> getStoryOwnerId(String authorization, String storyId) {
        return ResponseEntity.ok(myStoriesController.getStoryOwnerId(authorization, storyId));
    }

    @Override
    public ResponseEntity<GetStorySeenByUsersRespond> getStorySeenByUsers(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                                          @ApiParam(value = "",required=true) @PathVariable("storyId") String storyId,
                                                                          @ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") Integer page,
                                                                          @ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") Integer size) {
        return ResponseEntity.ok(myStoriesController.getStorySeenByUsers(authorization, storyId, page, size));
    }

    @Override
    public ResponseEntity<Void> removeStory(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                            @ApiParam(value = "the storyId.",required=true) @PathVariable("storyId") String storyId) {
        myStoriesController.removeStory(authorization, storyId);
        return ResponseEntity.noContent().build();
    }

    @Override
	public ResponseEntity<UploadUrlResponse> getUploadUrl(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@NotNull @ApiParam(value = "the file key that will be uploaded.", required = true) @Valid @RequestParam(value = "fileKey", required = true) String fileKey) {
		UploadUrlResponse response = myStoriesController.getUploadUrl(authorization, fileKey);
		return ResponseEntity.ok(response);
	}

	@Override
	public ResponseEntity<PublicUserInfoRespond> getSeenUsersByStoryId(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "storyId", required = true) @PathVariable("storyId") String storyId,
			@ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
		return ResponseEntity.ok(myStoriesController.getSeenUsersByStoryId(authorization, storyId, page, size));
	}

	@Override
	public ResponseEntity<GetMyStoriesRespond> getUserStories(
			@ApiParam(value = "", required = true) @RequestHeader(value = "Authorization", required = true) String authorization,
			@ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
			@ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
		return ResponseEntity.ok(myStoriesController.getUserStories(authorization, page, size));
	}
}
