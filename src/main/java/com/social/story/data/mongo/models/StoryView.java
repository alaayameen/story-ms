package com.social.story.data.mongo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

/**
 * @author ayameen
 *
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Document(collection = "story_views")
public class StoryView extends AbstractAuditing {
	
	@Id
	private String id;
	
	@Indexed
	private String storyId;
	
	@Indexed
	private String userId;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		StoryView storyView = (StoryView) o;
		return 	Objects.equals(storyId, storyView.storyId) &&
				Objects.equals(userId, storyView.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(storyId, userId);
	}
}
