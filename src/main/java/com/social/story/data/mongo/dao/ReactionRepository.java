/**
 * 
 */
package com.social.story.data.mongo.dao;

import java.util.List;
import java.util.Optional;

import com.social.story.data.mongo.models.Reaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReactionRepository extends MongoRepository<Reaction, String> {

	public Optional<Reaction> findByStoryIdAndUserId(String storyId, String userId);

	public Page<Reaction> findByStoryId(String storyId, Pageable pageable);

	public long deleteByStoryId(String storyId);

	public long deleteByStoryIdIn(List<String> storyIdList);
}
