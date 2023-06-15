package com.social.story.data.mongo.dao;

import com.social.story.data.mongo.models.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author ayameen
 *
 */
@Repository
public interface StoryRepository extends MongoRepository<Story, String> {

    Page<Story> findByUserId(String userId, Pageable pageable);
    List<Story> findByUserId(String userId);
    void deleteByUserId(String userId);
    Page<Story> findByUserIdAndCreatedDtGreaterThanOrderByCreatedDtDesc(String userId, LocalDateTime dateMinus24Hours, Pageable pageable);
    List<Story> findByIdIn(List<String> chunk);
    void deleteByIdIn(List<String> singletonList);
}
