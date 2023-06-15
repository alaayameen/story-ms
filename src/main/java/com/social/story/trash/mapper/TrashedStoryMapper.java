package com.social.story.trash.mapper;

import com.social.story.data.mongo.models.Story;
import com.social.story.trash.enums.ContentCommand;
import com.social.story.trash.model.Trash;
import com.social.story.trash.utils.Constants;
import com.social.story.trash.utils.SerializationHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class TrashedStoryMapper {

    SerializationHandler serializationHandler;

    public Trash toTrash(Story story, String command) {
        return Trash.builder()
                .command(ContentCommand.fromValue(command))
                .deletedDate(LocalDateTime.now())
                .classForName(story.getClass().getName())
                .entityId(story.getId())
                .entityName(Constants.CONTENT_TYPE.STORY.name())
                .userId(story.getUserId())
                .serializedEntity(serializationHandler.serializeEntity(story))
                .build();
    }

    public List<Trash> toTrashes(List<Story> stories, String command) {
        return stories.stream().map(story -> toTrash(story, command)).collect(Collectors.toList());
    }

    public Story toStory(Trash trash) {
        return (Story) serializationHandler.deSerializeEntity(trash.getSerializedEntity(), trash.getClassForName());
    }
}
