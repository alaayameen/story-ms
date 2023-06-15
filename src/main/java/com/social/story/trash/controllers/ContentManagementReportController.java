package com.social.story.trash.controllers;

import com.social.core.models.UserInfo;
import com.social.story.trash.services.ContentManagementReportService;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.contentmanagement.DeleteOperationsCriteria;
import com.social.swagger.model.contentmanagement.GetDeleteOperationsReportRespond;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;

@Log4j2
@Controller
@AllArgsConstructor
public class ContentManagementReportController {
	
	private final ContentManagementReportService contentManagementReportService;
    private final StoryHelper helper;


    public GetDeleteOperationsReportRespond getSoftDeleteOperations(String authorization, Integer page, Integer size, DeleteOperationsCriteria criteria) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        log.debug("A request to getSoftDeleteOperations: {} by user Id {}", criteria, userInfo.getId());
        GetDeleteOperationsReportRespond getHardDeleteReportRespond = contentManagementReportService.getSoftDeleteOperations(authorization, page, size, criteria);
        log.debug("getSoftDeleteOperations called successfully by user Id {}, with {}", userInfo.getId(), criteria);
        return getHardDeleteReportRespond;
    }

}
