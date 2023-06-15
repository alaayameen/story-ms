package com.social.story.mappers;

import com.social.story.data.mongo.models.MediaDetails;

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
public class MediaDetailsMapper implements ObjectMapper<com.social.swagger.model.story.MediaDetails, MediaDetails> {

    SoundDetailsMapper soundDetailsMapper;

    @Override
    public MediaDetails toTarget(com.social.swagger.model.story.MediaDetails source) {
        return MediaDetails.builder()
                .mediaType(source.getMediaType().name())
                .mediaUrl(source.getMediaUrl())
                .thumb(source.getThumb())
                .soundDetails(soundDetailsMapper.toTarget(source.getSoundDetails()))
                .build();

    }

    @Override
    public List<MediaDetails> toTargets(List<com.social.swagger.model.story.MediaDetails> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public com.social.swagger.model.story.MediaDetails toSource(MediaDetails target) {
        com.social.swagger.model.story.MediaDetails mediaDetails = new com.social.swagger.model.story.MediaDetails();
        mediaDetails.setMediaType(com.social.swagger.model.story.MediaDetails.MediaTypeEnum.fromValue(target.getMediaType()));
        mediaDetails.setMediaUrl(target.getMediaUrl());
        mediaDetails.setThumb(target.getThumb());
        mediaDetails.setSoundDetails(soundDetailsMapper.toSource(target.getSoundDetails()));
        return mediaDetails;
    }

    @Override
    public List<com.social.swagger.model.story.MediaDetails> toSources(List<MediaDetails> targetsList) {
        return targetsList == null ? Collections.emptyList() : targetsList.stream().map(this::toSource).collect(Collectors.toList());
    }
}
