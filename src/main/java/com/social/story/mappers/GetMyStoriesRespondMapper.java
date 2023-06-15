package com.social.story.mappers;

import com.social.story.data.mongo.models.Story;
import com.social.swagger.model.story.GetMyStoriesRespond;
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
public class GetMyStoriesRespondMapper implements ObjectMapper<Page<Story>, GetMyStoriesRespond> {

    MyUserStoryDetailsMapper myUserStoryDetailsMapper;

    @Override
    public GetMyStoriesRespond toTarget(Page<Story> source) {
        GetMyStoriesRespond getMyStoriesRespond = new GetMyStoriesRespond();
        getMyStoriesRespond.setData(myUserStoryDetailsMapper.toTargets(source.getContent()));
        getMyStoriesRespond.setPageNumber(source.getNumber() + 1);
        getMyStoriesRespond.setPageSize(source.getSize());
        getMyStoriesRespond.setTotalPages(source.getTotalPages());
        getMyStoriesRespond.setTotalElements(source.getTotalElements());
        return getMyStoriesRespond;
    }

    @Override
    public List<GetMyStoriesRespond> toTargets(List<Page<Story>> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public Page<Story> toSource(GetMyStoriesRespond target) {
        return null;
    }

    @Override
    public List<Page<Story>> toSources(List<GetMyStoriesRespond> targetsList) {
        return Collections.emptyList();
    }
}
