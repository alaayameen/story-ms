package com.social.story.mappers;

import com.social.story.data.mongo.models.Hashtag;
import com.social.swagger.model.story.HashtagDetails;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@Service
public class HashtagMapper implements ObjectMapper<HashtagDetails, Hashtag> {

    @Override
    public Hashtag toTarget(HashtagDetails source) {
        return Hashtag.builder()
                .id(source.getId())
                .text(source.getText())
                .build();
    }

    @Override
    public List<Hashtag> toTargets(List<HashtagDetails> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public HashtagDetails toSource(Hashtag target) {
        HashtagDetails hashtagDetails = new HashtagDetails();
        hashtagDetails.setId(target.getId());
        hashtagDetails.text(target.getText());
        return hashtagDetails;
    }

    @Override
    public List<HashtagDetails> toSources(List<Hashtag> targetsList) {
        return targetsList == null ? Collections.emptyList() : targetsList.stream().map(this::toSource).collect(Collectors.toList());
    }
}
