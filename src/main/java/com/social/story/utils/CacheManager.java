/**
 * 
 */
package com.social.story.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.social.swagger.called.user.model.PublicUserInfo;
import com.social.swagger.called.user.model.RetrieveUsersInfosByIdsRespond;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import com.social.core.utils.JWTUtil;
import com.social.story.gateway.UsersApiClient;

import lombok.extern.log4j.Log4j2;

/**
 * @author mshawahneh
 *
 */
@Component
@Log4j2
public class CacheManager {

	private static Map<String, PublicUserInfo> usersCache = new ConcurrentHashMap<>();
	
	private JWTUtil jwtUtil;
	
	private UsersApiClient usersApi;
	
	public CacheManager(JWTUtil jwtUtils, UsersApiClient usersApi) {
		this.jwtUtil = jwtUtils;
		this.usersApi = usersApi;
	}
	
	public Map<String, PublicUserInfo> getUsersInfoReport(String authorization ,List<String> distinctUserList){
		
		Map<String, PublicUserInfo> usersReport = new HashMap<>();
		List<String> missingUsersInCache = new ArrayList<>();
		distinctUserList.forEach(userId -> {
			
			PublicUserInfo userInfo = usersCache.get(userId);
			if(Objects.isNull(userInfo)) {
				missingUsersInCache.add(userId);
			}else {
				usersReport.put(userId, userInfo);
			}
		});
		
		if(missingUsersInCache.isEmpty()) {
			return usersReport;
		}
		
		Map<String, PublicUserInfo> newUsersToCache = retrieveUsersForCache(authorization, missingUsersInCache);
		usersReport.putAll(newUsersToCache);
		
		return usersReport;
	}
	
	@Scheduled(fixedRate = 60000 * 5)
	public void updateCache() {
		String authorization = "Bearer " + jwtUtil.generateTokenForAnonymousUser();
		try {
			RetrieveUsersInfosByIdsRespond usersRespond = usersApi.retrieveUpdatedUsersList(authorization, 6);
			if(Objects.nonNull(usersRespond) && !ObjectUtils.isEmpty(usersRespond.getUsersDetails())) {
				//Here we have some updated users. we need to store existing users only
				for (PublicUserInfo user: usersRespond.getUsersDetails()) {
					PublicUserInfo cachedUser = usersCache.get(user.getUserId());
					if(Objects.nonNull(cachedUser)) {
						cachedUser.setUserName(user.getUserName());
						cachedUser.setProfilePictureUrl(user.getProfilePictureUrl());
						
					}
				}
				
			}
		} catch (Exception e) {
			log.error("USER_SERVICE_FAILED for user getting updated users list {}");
		}
	}
	private Map<String, PublicUserInfo> retrieveUsersForCache(String authorization ,List<String> distinctUserList) {
		log.debug("Read users Inforamtion from users {} service for user", distinctUserList);
		
		if(Objects.isNull(authorization)) {
			authorization = "Bearer " + jwtUtil.generateTokenForAnonymousUser();
		}
		
		try {
			RetrieveUsersInfosByIdsRespond  usersRespond = usersApi.retrieveUsersByIdList(authorization, distinctUserList);
			if(Objects.isNull(usersRespond) || 
					ObjectUtils.isEmpty(usersRespond.getUsersDetails())) {
					
				log.error("Error while retrieving users from user-ms, "
						+ "returned data does not satify expectations , sent userList {}", distinctUserList);
				usersRespond = new RetrieveUsersInfosByIdsRespond()
								.usersDetails(new ArrayList<>());
			}
			
			Map<String, PublicUserInfo> usersMap =  usersRespond.getUsersDetails()
					.stream()
					.collect(
							Collectors.toMap(
									user -> user.getUserId(), 
									user -> user
									)
							);
			usersCache.putAll(usersMap);
			
			return usersMap;
			
		}catch (Exception e) {
			log.error("USER_SERVICE_FAILED for user list {}", distinctUserList);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "FETCHING_USERS_FAILED");
		}
	}
	
	

}
