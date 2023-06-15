package com.social.story.trash.mapper;

import com.social.core.utils.commonUtils;
import com.social.story.data.mongo.models.Story;
import com.social.story.mappers.ObjectMapper;
import com.social.swagger.model.contentmanagement.StoryDetails;
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
public class TrashedStoryDetailsMapper implements ObjectMapper<Story, StoryDetails> {

    TrashedHashtagMapper trashedHashtagMapper;

    TrashedMediaDetailsMapper trashedMediaDetailsMapper;
    
    commonUtils commonUtils;

    @Override
    public StoryDetails toTarget(Story source) {
        StoryDetails storyDetails =  new StoryDetails();
        storyDetails.setCaption(source.getCaption());
        storyDetails.setHashtags(trashedHashtagMapper.toTargets(source.getHashtags()));
        storyDetails.setStoryId(source.getId());
        storyDetails.setCreationTime(commonUtils.convertLocalDateTimeToFormattedOffsetDateTime(source.getCreatedDt()));
        storyDetails.setMediaDetails(trashedMediaDetailsMapper.toTarget(source.getMediaDetails()));
        return storyDetails;
    }

    @Override
    public List<StoryDetails> toTargets(List<Story> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Story toSource(StoryDetails target) {
        return null;
    }

    @Override
    public List<Story> toSources(List<StoryDetails> targetsList) {
        return Collections.emptyList();
    }
}
