/**
 * 
 */
package com.social.story.data.mongo.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import com.social.story.consts.StoryConstants;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@Document(collection = StoryConstants.REACTION_COLLECTIN)
@CompoundIndexes({ @CompoundIndex(name = "user_story", def = "{ 'userId' : 1, 'storyId' : 1 }") })
public class Reaction extends AbstractAuditingWithDeleteFields implements StoryConstants {

	@Id
	private String id;

	@Indexed
	private String userId;

	@Indexed
	@Field(name = STORY_ID_FIELD, targetType = FieldType.OBJECT_ID)
	private String storyId;

	@Field(REACTION_TYPE_FIELD)
	private ReactionType reactionType;
}
