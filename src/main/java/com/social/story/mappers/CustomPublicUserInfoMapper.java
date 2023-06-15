package com.social.story.mappers;

import com.social.swagger.model.story.CustomPublicUserInfo;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@AllArgsConstructor
@Service
public class CustomPublicUserInfoMapper implements ObjectMapper<com.social.story.mappers.CustomPublicUserInfo, CustomPublicUserInfo> {


    @Override
    public CustomPublicUserInfo toTarget(com.social.story.mappers.CustomPublicUserInfo source) {
        CustomPublicUserInfo publicUserInfo =  new CustomPublicUserInfo();
        if (source != null) {
            publicUserInfo.setUserId(source.getUserId());
            publicUserInfo.setFollowedByMe(source.getFollowedByMe());
            publicUserInfo.setUserName(source.getUserName());
            publicUserInfo.setProfilePictureUrl(source.getProfilePictureUrl());
        } else {
            publicUserInfo = null;
        }

        return publicUserInfo;
    }

    @Override
    public List<CustomPublicUserInfo> toTargets(List<com.social.story.mappers.CustomPublicUserInfo> sourceList) {
        return sourceList == null ? Collections.emptyList() : sourceList.stream().map(this::toTarget).collect(Collectors.toList());
    }

    @Override
    public com.social.story.mappers.CustomPublicUserInfo toSource(CustomPublicUserInfo target) {
        com.social.story.mappers.CustomPublicUserInfo publicUserInfo = new com.social.story.mappers.CustomPublicUserInfo();
        publicUserInfo.setUserId(target.getUserId());
        publicUserInfo.setUserName(target.getUserName());
        publicUserInfo.setProfilePictureUrl(target.getProfilePictureUrl());
        return publicUserInfo;
    }

    @Override
    public List<com.social.story.mappers.CustomPublicUserInfo> toSources(List<CustomPublicUserInfo> targetsList) {
        return targetsList == null ? Collections.emptyList() : targetsList.stream().map(this::toSource).collect(Collectors.toList());
    }
}
