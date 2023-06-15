package com.social.story.trash.services;


import com.social.swagger.model.contentmanagement.DeleteOperationsCriteria;
import com.social.swagger.model.contentmanagement.GetDeleteOperationsReportRespond;

public interface ContentManagementReportService {

    /**
     * Get soft deleted users report with filter criteria
     *
     * @param authorization logged in user auth token
     * @param page number
     * @param size of page
     * @param criteria details to filter based on
     * @return GetHardDeleteReportRespond
     */
    GetDeleteOperationsReportRespond getSoftDeleteOperations(String authorization, Integer page, Integer size, DeleteOperationsCriteria criteria);
}
