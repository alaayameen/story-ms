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
public class SoundDetails {

    String soundUrl;
    String soundId;
}
