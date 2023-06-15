package com.social.story.mappers;

import com.social.story.data.mongo.models.UserStories;
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
public class UserStoriesMapper implements ObjectMapper<UserStories, com.social.swagger.model.story.UserStories> {
    PublicUserInfoMapper publicUserInfoMapper;
    FollowingsStoriesMapper followingsStoriesMapper;

    @Override
    public com.social.swagger.model.story.UserStories toTarget(UserStories source) {
        com.social.swagger.model.story.UserStories userStories = new com.social.swagger.model.story.UserStories();
        userStories.setUserDetails(publicUserInfoMapper.toTarget(source.getPublicUserInfo()));
        userStories.setUserStories(followingsStoriesMapper.toTargets(source.getStories()));
        return userStories;
    }

    @Override
    public List<com.social.swagger.model.story.UserStories> toTargets(List<UserStories> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public UserStories toSource(com.social.swagger.model.story.UserStories target) {
        return null;
    }

    @Override
    public List<UserStories> toSources(List<com.social.swagger.model.story.UserStories> targetsList) {
        return Collections.emptyList();
    }
}
