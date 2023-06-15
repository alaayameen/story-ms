package com.social.story.data.mongo.models;

import lombok.Data;

@Data
public class StoryViewsCount {

	private String storyId;

	private int count;
}
