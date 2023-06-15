package com.social.story.data.mongo.dao;

import com.social.story.data.mongo.models.StoryView;
import com.social.story.data.mongo.models.StoryViewsCount;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author ayameen
 *
 */
@Repository
public interface StoryViewRepository extends MongoRepository<StoryView, String> {

	List<StoryView> findByStoryId(String storyId);

	List<StoryView> findByStoryIdIn(List<String> storiesIdList);

	@Aggregation(pipeline = { "{$match: { 'storyId': { $in : ?0} }}",
			"{$group: {_id:'$storyId', count: { $sum: 1 }  }}",
			"{$project: {storyId: $_id, count: $count}}"
	})
	List<StoryViewsCount> countByStoryIdIn(List<String> storiesIdList);

	List<String> findUserIdByStoryId(String storyId);

	Optional<StoryView> findByStoryIdAndUserId(String storyId, String userId);

	void deleteByStoryId(String storyId);

	Page<StoryView> findByStoryId(String storyId, Pageable pageable);

	List<StoryView> findIdByStoryIdIn(List<String> storyIds);

	void deleteByStoryIdIn(List<String> ids);
}
