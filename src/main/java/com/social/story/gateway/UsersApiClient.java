package com.social.story.gateway;

import com.social.core.utils.commonUtils;
import com.social.swagger.called.user.api.UsersApi;
import com.social.swagger.called.user.model.RetrieveUsersInfoByIdsRequest;
import com.social.swagger.called.user.model.RetrieveUsersInfosByIdsRespond;
import com.social.swagger.called.user.restclient.ApiClient;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author ayameen
 */
@Service
@AllArgsConstructor
public class UsersApiClient {
	
	ApiClient apiClient;
    UsersApi usersApi;
    commonUtils utils;

    public RetrieveUsersInfosByIdsRespond retrieveUsersByIdList(String authorization, List<String> userIds) {

        apiClient.setBasePath(utils.buildUrl("user-ms", apiClient.getBasePath()).toString());
        usersApi.setApiClient(apiClient);

        RetrieveUsersInfoByIdsRequest request=new RetrieveUsersInfoByIdsRequest();
        request.setUsersIds(userIds);
            return new RetrieveUsersInfosByIdsRespond();
//        return usersApi.retrieveUsersInfoByIds(authorization, request);
   }

    public RetrieveUsersInfosByIdsRespond retrieveUpdatedUsersList(String authorization, int period) {
   	 
        apiClient.setBasePath(utils.buildUrl("user-ms", apiClient.getBasePath()).toString());
        usersApi.setApiClient(apiClient);
       return new RetrieveUsersInfosByIdsRespond();
//        return usersApi.retrieveUpdatedUsersInfoByIds(authorization, new BigDecimal(period));
   }



}
