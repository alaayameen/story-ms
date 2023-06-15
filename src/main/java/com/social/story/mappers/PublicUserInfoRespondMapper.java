package com.social.story.mappers;

import com.social.swagger.called.user.model.PublicUserInfo;
import com.social.swagger.model.story.PublicUserInfoRespond;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class PublicUserInfoRespondMapper implements ObjectMapper<Page<PublicUserInfo>, PublicUserInfoRespond> {

	PublicUserInfoMapper publicUserInfoMapper;

	@Override
	public PublicUserInfoRespond toTarget(Page<PublicUserInfo> source) {
		PublicUserInfoRespond publicUserInfoRespond = new PublicUserInfoRespond();
		publicUserInfoRespond.setData(publicUserInfoMapper.toTargets(source.getContent()));
		publicUserInfoRespond.setPageNumber(source.getNumber() + 1);
		publicUserInfoRespond.setPageSize(source.getSize());
		publicUserInfoRespond.setTotalPages(source.getTotalPages());
		publicUserInfoRespond.setTotalElements(source.getTotalElements());
		return publicUserInfoRespond;
	}

	@Override
	public List<PublicUserInfoRespond> toTargets(List<Page<PublicUserInfo>> sourceList) {
		return sourceList == null ? Collections.emptyList()
				: sourceList.stream().map(this::toTarget).collect(Collectors.toList());
	}

	@Override
	public Page<PublicUserInfo> toSource(PublicUserInfoRespond target) {
		return null;
	}

	@Override
	public List<Page<PublicUserInfo>> toSources(List<PublicUserInfoRespond> targetsList) {
		return Collections.emptyList();
	}
}
