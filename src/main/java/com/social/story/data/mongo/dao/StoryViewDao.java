package com.social.story.data.mongo.dao;

import com.social.story.data.mongo.models.StoryView;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@Log4j2
@Component
@AllArgsConstructor
public class StoryViewDao {

    MongoConverter mongoConverter;
    MongoTemplate mongoTemplate;

    StoryViewRepository storyViewRepository;

    public List<String> findIdByStoryIdIn(List<String> storyIds) {
        return storyViewRepository.findIdByStoryIdIn(storyIds).stream().map(StoryView::getId).collect(Collectors.toList());
    }

    public List<StoryView> findByStoryIdIn(List<String> storyIds) {
        return storyViewRepository.findIdByStoryIdIn(storyIds);
    }

    public void insertAllIgnoreAuditing(List<StoryView> stories) {
        List<Document> documents = stories.stream().map(story -> {
            Document updateDoc = new Document();
            mongoConverter.write(story,  updateDoc);
            return updateDoc;
        }).collect(Collectors.toList());

        mongoTemplate.getCollection("story_views").insertMany(documents);
    }

    public void deleteAll(List<StoryView> storyViews) {
        storyViewRepository.deleteAll(storyViews);
    }

    public void deleteByStoryIdIn(List<String> ids) {
        storyViewRepository.deleteByStoryIdIn(ids);
    }
}
