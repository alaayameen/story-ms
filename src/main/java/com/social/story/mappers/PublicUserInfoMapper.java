package com.social.story.mappers;

import com.social.swagger.called.user.model.PublicUserInfo;
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
public class PublicUserInfoMapper
		implements ObjectMapper<PublicUserInfo, com.social.swagger.model.story.PublicUserInfo> {

	SoundDetailsMapper soundDetailsMapper;

	@Override
	public com.social.swagger.model.story.PublicUserInfo toTarget(PublicUserInfo source) {
		com.social.swagger.model.story.PublicUserInfo publicUserInfo = new com.social.swagger.model.story.PublicUserInfo();
		publicUserInfo.setUserId(source.getUserId());
		publicUserInfo.setUserName(source.getUserName());
		publicUserInfo.setProfilePictureUrl(source.getProfilePictureUrl());
		return publicUserInfo;
	}

	@Override
	public List<com.social.swagger.model.story.PublicUserInfo> toTargets(List<PublicUserInfo> sourceList) {
		return sourceList == null ? Collections.emptyList()
				: sourceList.stream().filter(userInfo -> userInfo != null).map(this::toTarget)
						.collect(Collectors.toList());
	}

	@Override
	public PublicUserInfo toSource(com.social.swagger.model.story.PublicUserInfo target) {
		PublicUserInfo publicUserInfo = new PublicUserInfo();
		publicUserInfo.setUserId(target.getUserId());
		publicUserInfo.setUserName(target.getUserName());
		publicUserInfo.setProfilePictureUrl(target.getProfilePictureUrl());
		return publicUserInfo;
	}

	@Override
	public List<PublicUserInfo> toSources(List<com.social.swagger.model.story.PublicUserInfo> targetsList) {
		return targetsList == null ? Collections.emptyList()
				: targetsList.stream().map(this::toSource).collect(Collectors.toList());
	}
}
