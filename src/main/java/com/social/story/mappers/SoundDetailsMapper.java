package com.social.story.mappers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 *
 */
@AllArgsConstructor
@Service
public class SoundDetailsMapper implements ObjectMapper<com.social.swagger.model.story.SoundDetails, com.social.story.data.mongo.models.SoundDetails> {

    @Override
    public com.social.story.data.mongo.models.SoundDetails toTarget(com.social.swagger.model.story.SoundDetails source) {
        return com.social.story.data.mongo.models.SoundDetails.builder()
                .soundUrl(source.getSoundUrl())
                .soundId(source.getSoundId())
                .build();

    }

    @Override
    public List<com.social.story.data.mongo.models.SoundDetails> toTargets(List<com.social.swagger.model.story.SoundDetails> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public com.social.swagger.model.story.SoundDetails toSource(com.social.story.data.mongo.models.SoundDetails target) {
        com.social.swagger.model.story.SoundDetails soundDetails = new com.social.swagger.model.story.SoundDetails();
        soundDetails.setSoundId(target.getSoundId());
        soundDetails.setSoundUrl(target.getSoundUrl());
        return soundDetails;
    }

    @Override
    public List<com.social.swagger.model.story.SoundDetails> toSources(List<com.social.story.data.mongo.models.SoundDetails> targetsList) {
        return targetsList == null ? Collections.emptyList() : targetsList.stream().map(this::toSource).collect(Collectors.toList());
    }
}
