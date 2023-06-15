package com.social.story.mappers;

import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.GetFollowingStoriesRespond;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class GetFollowingStoriesRespondMapper implements ObjectMapper<Page<Story>, GetFollowingStoriesRespond> {

    UsersStoriesMapper usersStoriesMapper;

    @Override
    public GetFollowingStoriesRespond toTarget(Page<Story> source) {
        GetFollowingStoriesRespond getFollowingStoriesRespond = new GetFollowingStoriesRespond();
        getFollowingStoriesRespond.setData(usersStoriesMapper.toTargets(source.getContent()));
        getFollowingStoriesRespond.setPageNumber(source.getNumber() + 1);
        getFollowingStoriesRespond.setPageSize(source.getSize());
        getFollowingStoriesRespond.setTotalPages(source.getTotalPages());
        getFollowingStoriesRespond.setTotalElements(source.getTotalElements());
        return getFollowingStoriesRespond;
    }

    @Override
    public List<GetFollowingStoriesRespond> toTargets(List<Page<Story>> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Page<Story> toSource(GetFollowingStoriesRespond target) {
        return null;
    }

    @Override
    public List<Page<Story>> toSources(List<GetFollowingStoriesRespond> targetsList) {
        return Collections.emptyList();
    }
}
