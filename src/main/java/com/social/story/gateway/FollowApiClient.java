package com.social.story.gateway;

import com.social.core.models.UserInfo;
import com.social.core.utils.JWTUtil;
import com.social.core.utils.commonUtils;
import com.social.swagger.called.follow.api.FollowApi;
import com.social.swagger.called.follow.model.GetFollowingStatusRequest;
import com.social.swagger.called.follow.model.GetFollowingStatusRespond;
import com.social.swagger.called.follow.model.ShareContentRequest;
import com.social.swagger.called.follow.restclient.ApiClient;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ayameen
 *
 */
@Service
@AllArgsConstructor
@Log4j2
public class FollowApiClient {

	ApiClient apiClient;
	commonUtils utils;
	FollowApi followApi;
	JWTUtil jwtUtil;

	public List<String> getUserFollowingsIds(String authorization, Integer page, Integer size) {
		UserInfo userInfo = jwtUtil.getUserInfoFromToken();
		String userId = userInfo.getId();
		log.debug("Call getUserFollowingsIds API by user {} ", userId);

		apiClient.setBasePath(utils.buildUrl("follow-ms", apiClient.getBasePath()).toString());
		followApi.setApiClient(apiClient);

		ShareContentRequest shareContentRequest = new ShareContentRequest();
		shareContentRequest.setContentType(ShareContentRequest.ContentTypeEnum.STORY);
		return followApi.getSharingFollowings(authorization, BigDecimal.valueOf(page), BigDecimal.valueOf(size),
				shareContentRequest);
	}

	@Async
	public void trackSharedContent(String authorization, String userId, ShareContentRequest shareContentRequest) {
		log.debug("Call shareContent API by user {} to update story last time", userId);
//		apiClient.setBasePath(utils.buildUrl("follow-ms", apiClient.getBasePath()).toString());
//		followApi.setApiClient(apiClient);
//		followApi.trackSharedContent(authorization, userId, shareContentRequest);
	}

	public GetFollowingStatusRespond getFollowingStatus(String authorization,
														GetFollowingStatusRequest getFollowingStatusRequest) {
		UserInfo userInfo = jwtUtil.getUserInfoFromToken();
		String userId = userInfo.getId();
		log.debug("Call getFollowingStatus API by user {} ", userId);

		apiClient.setBasePath(utils.buildUrl("follow-ms", apiClient.getBasePath()).toString());
		followApi.setApiClient(apiClient);

		return followApi.getFollowingStatus(authorization, getFollowingStatusRequest);
	}
}
