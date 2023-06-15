package com.social.story.data.mongo.models;

import com.social.swagger.called.user.model.PublicUserInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author ayameen
 *
 */
@Getter
@Setter
@NoArgsConstructor
public class UserStories {

    String id;

    PublicUserInfo publicUserInfo;

    List<Story> stories;
}
