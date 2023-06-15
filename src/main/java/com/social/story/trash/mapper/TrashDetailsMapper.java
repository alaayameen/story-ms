package com.social.story.trash.mapper;

import com.social.story.data.mongo.models.Story;
import com.social.story.mappers.ObjectMapper;
import com.social.story.trash.utils.SerializationHandler;
import com.social.story.trash.model.Trash;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.contentmanagement.TrashDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class TrashDetailsMapper implements ObjectMapper<Trash, TrashDetails> {

    StoryHelper helper;
    com.social.core.utils.commonUtils commonUtils;
    TrashedStoryDetailsMapper trashedStoryDetailsMapper;
    SerializationHandler serializationHandler;

    @Override
    public TrashDetails toTarget(Trash source) {
        TrashDetails hardTrashDetails =  new TrashDetails();
        if (source != null) {
            hardTrashDetails
                    .command(source.getCommand().name())
                    .deleteBy(source.getDeletedBy())
                    .reportId(source.getReportingId())
                    .userId(source.getUserId())
                    .storyDetails(trashedStoryDetailsMapper.toTarget((Story) serializationHandler.deSerializeEntity(source.getSerializedEntity(), source.getClassForName())))
                    .deleteDate(commonUtils.convertLocalDateTimeToFormattedOffsetDateTime(source.getDeletedDate()));
        }
        return hardTrashDetails;
    }

    @Override
    public List<TrashDetails> toTargets(List<Trash> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Trash toSource(TrashDetails target) {
        return null;
    }

    @Override
    public List<Trash> toSources(List<TrashDetails> targetsList) {
        return Collections.emptyList();
    }
}
