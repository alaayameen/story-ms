package com.social.story.services;

import com.social.story.data.mongo.models.Hashtag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @author ayameen
 *
 */
@Service
public interface HashtagService {

    /**
     * Get or Create this hashtag based on what attribute is filled, if Id: get the existing hashtag
     * if the text is filled, return it if already exist with different id or create it.
     *
     * @param hashtag to be checked
     * @return existing hashtag
     */
    Hashtag getOrCreate(Hashtag hashtag);

    /**
     * Search for story hashtags
     *
     * @param searchText filter hashtags by this text
     * @param pageable pagination details
     * @return @{@link Page<Hashtag>} page of hashtags based on @pageable details
     */
    Page<Hashtag> searchHashtags(String searchText, Pageable pageable);
}
