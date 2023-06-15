package com.social.story.data.mongo.dao;

import com.social.story.data.mongo.models.Story;
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
public class StoryDao {

    MongoConverter mongoConverter;
    MongoTemplate mongoTemplate;

    StoryRepository storyRepository;
    public List<String> findIdByUserId(String userId) {
        return storyRepository.findByUserId(userId).stream().map(Story::getId).collect(Collectors.toList());
    }

    public void insertAllIgnoreAuditing(List<Story> stories) {
        List<Document> documents = stories.stream().map(post -> {
            Document updateDoc = new Document();
            mongoConverter.write(post,  updateDoc);
            return updateDoc;
        }).collect(Collectors.toList());

        mongoTemplate.getCollection("story").insertMany(documents);
    }

    public List<Story> findByIdIn(List<String> chunk) {
        return storyRepository.findByIdIn(chunk);
    }

    public void deleteAll(List<Story> stories) {
        storyRepository.deleteAll(stories);
    }

    public void deleteByIdIn(List<String> singletonList) {
        storyRepository.deleteByIdIn(singletonList);
    }
}
