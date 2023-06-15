package com.social.story.mappers;


import com.social.swagger.called.user.model.PublicUserInfo;
import lombok.Data;

/**
 * @author ayameen
 */
@Data
public class CustomPublicUserInfo extends PublicUserInfo {
    private Boolean followedByMe;


}
