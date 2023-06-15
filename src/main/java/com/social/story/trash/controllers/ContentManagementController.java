package com.social.story.trash.controllers;

import com.social.core.models.UserInfo;
import com.social.story.trash.services.ContentManagementService;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.contentmanagement.ContentManagementRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@AllArgsConstructor
public class ContentManagementController {
	
	private final ContentManagementService contentManagementService;
    private final StoryHelper helper;

    public void manageContentByContentId(String authorization, ContentManagementRequest contentManagementRequest) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to manageContentByContentIds: {} by user Id {}", contentManagementRequest, userInfo.getId());
        contentManagementService.manageContentById(authorization, contentManagementRequest);
        log.debug("manageContentByContentIds called successfully by user Id {}, with {}", userInfo.getId(), contentManagementRequest);
    }
}
