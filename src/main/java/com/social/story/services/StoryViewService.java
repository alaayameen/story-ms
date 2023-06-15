package com.social.story.services;

import com.social.story.data.mongo.models.StoryView;
import com.social.story.data.mongo.models.StoryViewsCount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author ayameen
 */
@Service
public interface StoryViewService {

	Optional<StoryView> findByStoryIdAndUserId(String storyId, String userId);

	List<StoryView> findByStoryId(String videoId);

	List<String> findUserIdByStoryId(String storyId);

	List<StoryView> findUserIdByStoryId(List<String> storiesIdList);

	List<StoryViewsCount> countByStoryId(List<String> storiesIdList);

	Page<StoryView> findByStoryId(String storyId, Pageable pageable);

	void save(StoryView view);

	void addViewerToStory(String userId, String storyId);
}
