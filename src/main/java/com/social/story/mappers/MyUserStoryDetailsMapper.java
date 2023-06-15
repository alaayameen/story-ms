package com.social.story.mappers;

import com.social.core.utils.commonUtils;
import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.MyUserStoryDetails;
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
public class MyUserStoryDetailsMapper implements ObjectMapper<Story, MyUserStoryDetails> {
    HashtagMapper hashtagMapper;
    MediaDetailsMapper mediaDetailsMapper;
    PublicUserInfoMapper publicUserInfoMapper;
    commonUtils commonUtils;
    
    @Override
    public MyUserStoryDetails toTarget(Story source) {
        MyUserStoryDetails myUserStoryDetails = new MyUserStoryDetails();
        myUserStoryDetails.setStoryId(source.getId());
        myUserStoryDetails.setCreationTime(commonUtils.convertLocalDateTimeToFormattedOffsetDateTime(source.getCreatedDt()));

        myUserStoryDetails.setCaption(source.getCaption());
        myUserStoryDetails.setHashtags(hashtagMapper.toSources(source.getHashtags()));
        myUserStoryDetails.setMediaDetails(mediaDetailsMapper.toSource(source.getMediaDetails()));
        myUserStoryDetails.setSeenBy(publicUserInfoMapper.toTargets(source.getSeenBy()));
        myUserStoryDetails.setSeenUsersNumber(source.getUsersSeenNumber());
        return myUserStoryDetails;
    }

    @Override
    public List<MyUserStoryDetails> toTargets(List<Story> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Story toSource(MyUserStoryDetails target) {
        return null;
    }

    @Override
    public List<Story> toSources(List<MyUserStoryDetails> targetsList) {
        return Collections.emptyList();
    }
}
