package com.social.story.trash.mapper;

import com.social.story.data.mongo.models.SoundDetails;
import com.social.story.mappers.ObjectMapper;
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
public class TrashedSoundDetailsMapper implements ObjectMapper<SoundDetails, com.social.swagger.model.contentmanagement.SoundDetails> {

    @Override
    public com.social.swagger.model.contentmanagement.SoundDetails toTarget(SoundDetails source) {
        return new com.social.swagger.model.contentmanagement.SoundDetails()
                .soundUrl(source.getSoundUrl())
                .soundId(source.getSoundId());

    }

    @Override
    public List<com.social.swagger.model.contentmanagement.SoundDetails> toTargets(List<SoundDetails> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public SoundDetails toSource(com.social.swagger.model.contentmanagement.SoundDetails target) {
        return null;
    }

    @Override
    public List<SoundDetails> toSources(List<com.social.swagger.model.contentmanagement.SoundDetails> targetsList) {
        return targetsList == null ? Collections.emptyList() : targetsList.stream().map(this::toSource).collect(Collectors.toList());
    }
}
