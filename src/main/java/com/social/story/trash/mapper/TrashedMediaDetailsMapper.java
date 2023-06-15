package com.social.story.trash.mapper;

import com.social.story.data.mongo.models.MediaDetails;
import com.social.story.mappers.ObjectMapper;
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
public class TrashedMediaDetailsMapper implements ObjectMapper<MediaDetails, com.social.swagger.model.contentmanagement.MediaDetails> {

    TrashedSoundDetailsMapper trashedSoundDetailsMapper;

    @Override
    public com.social.swagger.model.contentmanagement.MediaDetails toTarget(MediaDetails source) {
        com.social.swagger.model.contentmanagement.MediaDetails mediaDetails =  new com.social.swagger.model.contentmanagement.MediaDetails();
        mediaDetails.setMediaType(com.social.swagger.model.contentmanagement.MediaDetails.MediaTypeEnum.fromValue(source.getMediaType()));
        mediaDetails.setMediaUrl(source.getMediaUrl());
        mediaDetails.setThumb(source.getThumb());
        mediaDetails.soundDetails(trashedSoundDetailsMapper.toTarget(source.getSoundDetails()));
        return mediaDetails;
    }

    @Override
    public List<com.social.swagger.model.contentmanagement.MediaDetails> toTargets(List<MediaDetails> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public MediaDetails toSource(com.social.swagger.model.contentmanagement.MediaDetails target) {
        return null;
    }

    @Override
    public List<MediaDetails> toSources(List<com.social.swagger.model.contentmanagement.MediaDetails> targetsList) {
        return null;
    }


}
