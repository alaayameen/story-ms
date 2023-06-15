package com.social.story.controllers;

import com.social.story.data.mongo.models.Hashtag;
import com.social.story.mappers.HashtagMapper;
import com.social.story.services.HashtagService;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.story.SearchHashtagsResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

/**
 * @author ayameen
 */
@Log4j2
@Controller
@AllArgsConstructor
public class SearchController {

    StoryHelper helper;

    HashtagService hashtagService;

    HashtagMapper hashtagMapper;

    public SearchHashtagsResponse searchHashtags(String authorization, @Valid String searchText, @Valid Integer page, @Valid Integer size) {
        Pageable pageable =  helper.pageable(page, size,  Sort.by("createdDt").descending());
        Page<Hashtag> hashtags = Page.empty(pageable);

        try {
            hashtags = hashtagService.searchHashtags(searchText, pageable);
        } catch (Exception e) {
            log.error("Failed to search hashtags by {}",searchText);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "HASHTAG_SEARCH_FAILED");
        }

        SearchHashtagsResponse response = new SearchHashtagsResponse();
        response.setData(hashtagMapper.toSources(hashtags.getContent()));
        response.setPageNumber(hashtags.getNumber() + 1);
        response.setPageSize(hashtags.getSize());
        response.setTotalPages(hashtags.getTotalPages());
        response.setTotalElements(hashtags.getTotalElements());
        return response;
    }
}
