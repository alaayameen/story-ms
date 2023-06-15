package com.social.story.rest;

import com.social.story.controllers.SearchController;
import com.social.swagger.api.story.SearchApi;
import com.social.swagger.model.story.SearchHashtagsResponse;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author ayameen
 *
 * */
@RestController
@AllArgsConstructor
public class SearchRest implements SearchApi {

    SearchController searchController;

    @Override
    public ResponseEntity<SearchHashtagsResponse> searchHashtags(@ApiParam(value = "" ,required=true) @RequestHeader(value="Authorization", required=true) String authorization,
                                                          @ApiParam(value = "the text that searched for - optional input in case its empty will retrieve all.") @Valid @RequestParam(value = "searchText", required = false) String searchText,
                                                          @ApiParam(value = "Page Number to get.", defaultValue = "1") @Valid @RequestParam(value = "page", required = false, defaultValue="1") Integer page,
                                                          @ApiParam(value = "page size of items to return.", defaultValue = "10") @Valid @RequestParam(value = "size", required = false, defaultValue="10") Integer size) {
        SearchHashtagsResponse response = searchController.searchHashtags(authorization, searchText, page, size);
        return ResponseEntity.ok(response);
    }
}
