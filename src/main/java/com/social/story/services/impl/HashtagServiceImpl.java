package com.social.story.services.impl;

import com.social.story.data.mongo.models.Hashtag;
import com.social.story.data.mongo.dao.HashtagRepository;
import com.social.story.services.HashtagService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * @author ayameen
 *
 */
@Log4j2
@Component
@AllArgsConstructor
public class HashtagServiceImpl implements HashtagService {

    HashtagRepository hashtagRepository;

    /**
     * Get or Create this hashtag based on what attribute is filled, if Id: get the existing hashtag, else throw NOT_FOUND
     * if the text is filled, return it if already exists with different id or create it.
     *
     * @param hashtag to be checked
     * @return existing hashtag
     */
    public Hashtag getOrCreate(Hashtag hashtag) {
        if (hashtag == null) {
            return null;
        }

        if (hashtag.getId() != null && !hashtag.getId().isEmpty()) {
            Optional<Hashtag> hash = hashtagRepository.findById(hashtag.getId());
            if (hash.isPresent()) {
                return hash.get();
            } else {
                log.error("Hashtag Not Found {}", hashtag.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HASHTAG_NOT_FOUND");
            }
        } else if (hashtag.getText() != null){
            Optional<Hashtag> hash = hashtagRepository.findByText(hashtag.getText());
            if (hash.isPresent()) {
                return hash.get();
            } else {
                if (!hashtag.getText().isEmpty()) {
                    log.debug("request to create new hashtag: {}", hashtag.getText());
                    hashtag.setId(null);
                    hashtag = hashtagRepository.save(hashtag);
                    log.debug("hashtag : {} created successfully", hashtag.getText());
                    return hashtag;
                }
            }
        }
        return hashtag;
    }

    /**
     * Search for story hashtags
     *
     * @param searchText filter hashtags by this text
     * @param pageable pagination details
     * @return @{@link Page<Hashtag>} page of hashtags based on @pageable details
     */
    public Page<Hashtag> searchHashtags(String searchText, Pageable pageable){
        return hashtagRepository.findByTextContains(searchText, pageable);
    }


}
