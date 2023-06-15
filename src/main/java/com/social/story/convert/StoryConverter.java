/**
 * 
 */
package com.social.story.convert;

import com.social.story.data.mongo.models.Story;

/**
 * @author ideek
 *
 */
public interface StoryConverter {
	Story convertVideoToHls(Story story);
}