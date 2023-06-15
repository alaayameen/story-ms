package com.social.story.mappers;

import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.StoryDetails;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import com.social.core.utils.commonUtils;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class StoryDetailsMapper implements ObjectMapper<StoryDetails, Story> {

    HashtagMapper hashtagMapper;

    MediaDetailsMapper mediaDetailsMapper;
    
    commonUtils commonUtils;

    @Override
    public Story toTarget(StoryDetails source) {

        return Story.builder()
                .caption(source.getCaption())
                .hashtags(hashtagMapper.toTargets(source.getHashtags()))
                .mediaDetails(mediaDetailsMapper.toTarget(source.getMediaDetails())).build();
    }

    @Override
    public List<Story> toTargets(List<StoryDetails> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public StoryDetails toSource(Story target) {
        StoryDetails storyDetails =  new StoryDetails();
        storyDetails.setHashtags(hashtagMapper.toSources(target.getHashtags()));
        storyDetails.setStoryId(target.getId());
        storyDetails.setCaption(target.getCaption());
        storyDetails.setCreationTime(commonUtils.convertLocalDateTimeToFormattedOffsetDateTime(target.getCreatedDt()));
        
        storyDetails.setMediaDetails(mediaDetailsMapper.toSource(target.getMediaDetails()));
        return storyDetails;
    }

    @Override
    public List<StoryDetails> toSources(List<Story> targetsList) {
        return Collections.emptyList();
    }
}
