package com.social.story.mappers;

import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.UsersStories;
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
public class UsersStoriesMapper implements ObjectMapper<Story, UsersStories> {
    HashtagMapper hashtagMapper;
    MediaDetailsMapper mediaDetailsMapper;
    PublicUserInfoMapper publicUserInfoMapper;
    StoryDetailsMapper storyDetailsMapper;

    @Override
    public UsersStories toTarget(Story source) {
        UsersStories usersStories = new UsersStories();
        usersStories.setUserDetails(publicUserInfoMapper.toTarget(source.getPublicUserInfo()));
        usersStories.setSeen(source.getSeen());
        usersStories.setStoryDetails(storyDetailsMapper.toSource(source));
        return usersStories;
    }

    @Override
    public List<UsersStories> toTargets(List<Story> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Story toSource(UsersStories target) {
        return null;
    }

    @Override
    public List<Story> toSources(List<UsersStories> targetsList) {
        return Collections.emptyList();
    }
}
