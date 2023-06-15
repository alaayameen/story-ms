package com.social.story.mappers;

import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.FollowingsStories;
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
public class FollowingsStoriesMapper implements ObjectMapper<Story, FollowingsStories> {
    HashtagMapper hashtagMapper;
    MediaDetailsMapper mediaDetailsMapper;
    PublicUserInfoMapper publicUserInfoMapper;
    StoryDetailsMapper storyDetailsMapper;

    @Override
    public FollowingsStories toTarget(Story source) {
        FollowingsStories usersStories = new FollowingsStories();
        usersStories.setSeen(source.getSeen());
        usersStories.setStoryDetails(storyDetailsMapper.toSource(source));
        return usersStories;
    }

    @Override
    public List<FollowingsStories> toTargets(List<Story> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Story toSource(FollowingsStories target) {
        return null;
    }

    @Override
    public List<Story> toSources(List<FollowingsStories> targetsList) {
        return Collections.emptyList();
    }
}
