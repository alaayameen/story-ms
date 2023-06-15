/**
 * 
 */
package com.social.story.services;

import com.social.story.data.mongo.models.ReactionType;
import com.social.swagger.model.story.StoryReactionsResponse;

public interface ReactionsService {

	public void saveStoryReaction(String userId, String storyId, ReactionType reactionType);

	public StoryReactionsResponse getStoryReactions(String storyId, Integer page, Integer size);
}
