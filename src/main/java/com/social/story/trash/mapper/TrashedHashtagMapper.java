package com.social.story.trash.mapper;

import com.social.story.data.mongo.models.Hashtag;
import com.social.story.mappers.ObjectMapper;
import com.social.swagger.model.contentmanagement.HashtagDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@Service
public class TrashedHashtagMapper implements ObjectMapper<Hashtag, HashtagDetails> {

    @Override
    public HashtagDetails toTarget(Hashtag source) {
        return new HashtagDetails ()
                .id(source.getId())
                .text(source.getText());
    }

    @Override
    public List<HashtagDetails> toTargets(List<Hashtag> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Hashtag toSource(HashtagDetails target) {
        return null;
    }

    @Override
    public List<Hashtag> toSources(List<HashtagDetails> targetsList) {
        return targetsList == null ? Collections.emptyList() : targetsList.stream().map(this::toSource).collect(Collectors.toList());
    }
}
