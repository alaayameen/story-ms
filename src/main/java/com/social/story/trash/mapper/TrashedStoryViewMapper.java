package com.social.story.trash.mapper;

import com.social.story.data.mongo.models.StoryView;
import com.social.story.trash.enums.ContentCommand;
import com.social.story.trash.model.Trash;
import com.social.story.trash.utils.Constants;
import com.social.story.trash.utils.SerializationHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class TrashedStoryViewMapper {

    SerializationHandler serializationHandler;

    public Trash toTrash(StoryView storyView, String command, String parentId) {
        return Trash.builder()
                .command(ContentCommand.fromValue(command))
                .deletedDate(LocalDateTime.now())
                .classForName(storyView.getClass().getName())
                .entityId(storyView.getId())
                .entityName(Constants.CONTENT_TYPE.STORY_VIEW.name())
                .userId(storyView.getUserId())
                .parentId(parentId)
                .serializedEntity(serializationHandler.serializeEntity(storyView))
                .build();
    }

    public StoryView toStoryView(Trash trash) {
        return (StoryView) serializationHandler.deSerializeEntity(trash.getSerializedEntity(), trash.getClassForName());
    }
}
