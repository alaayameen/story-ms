package com.social.story.mappers;

import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.AddStoryRequest;
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
public class StoryMapper implements ObjectMapper<AddStoryRequest, Story> {

    HashtagMapper hashtagMapper;

    MediaDetailsMapper mediaDetailsMapper;

    @Override
    public Story toTarget(AddStoryRequest source) {

        return Story.builder()
                .caption(source.getCaption())
                .hashtags(hashtagMapper.toTargets(source.getHashtags()))
                .mediaDetails(mediaDetailsMapper.toTarget(source.getMediaDetails())).build();
    }

    @Override
    public List<Story> toTargets(List<AddStoryRequest> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public AddStoryRequest toSource(Story target) {
        return null;
    }

    @Override
    public List<AddStoryRequest> toSources(List<Story> targetsList) {
        return Collections.emptyList();
    }
}
