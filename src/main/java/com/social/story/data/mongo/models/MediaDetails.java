package com.social.story.data.mongo.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * @author ayameen
 *
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class MediaDetails {

        String mediaType;
        String mediaUrl;
        String thumb;
        SoundDetails soundDetails;
}
