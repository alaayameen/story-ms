package com.social.story.services.impl;


import com.social.story.data.mongo.dao.StoryViewRepository;
import com.social.story.data.mongo.dao.impl.StoryViewRepositoryImpl;
import com.social.story.data.mongo.models.StoryView;
import com.social.story.data.mongo.models.StoryViewsCount;
import com.social.story.services.StoryViewService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@Service
@AllArgsConstructor
public class StoryViewServiceImpl implements StoryViewService {
	
	StoryViewRepository storyViewRepository;
	StoryViewRepositoryImpl storyViewRepositoryImpl;

	public Optional<StoryView> findByStoryIdAndUserId(String storyId, String userId) {
		return storyViewRepository.findByStoryIdAndUserId(storyId, userId);
	}

	public List<StoryView> findByStoryId(String storyId) {
		return storyViewRepository.findByStoryId(storyId);
	}

	public List<String> findUserIdByStoryId(String storyId) {
		return storyViewRepository.findByStoryId(storyId).stream().map(StoryView::getUserId).collect(Collectors.toList());
	}

	public List<StoryView> findUserIdByStoryId(List<String> storiesIdList) {
		return storyViewRepository.findByStoryIdIn(storiesIdList);
	}

	public List<StoryViewsCount> countByStoryId(List<String> storiesIdList) {
		return storyViewRepository.countByStoryIdIn(storiesIdList);
	}

	@Override
	public Page<StoryView> findByStoryId(String storyId, Pageable pageable) {
		return storyViewRepository.findByStoryId(storyId, pageable);
	}

	public void save(StoryView view) {
		storyViewRepository.save(view);
	}

	public void addViewerToStory(String userId, String storyId) {
		storyViewRepositoryImpl.addViewerToStory(userId, storyId);
	}
}
