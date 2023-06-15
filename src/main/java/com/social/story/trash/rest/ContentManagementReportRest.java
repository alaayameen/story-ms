package com.social.story.trash.rest;

import com.social.story.trash.controllers.ContentManagementReportController;
import com.social.swagger.api.contentmanagement.ContentManagementReportApi;
import com.social.swagger.model.contentmanagement.DeleteOperationsCriteria;
import com.social.swagger.model.contentmanagement.GetDeleteOperationsReportRespond;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
public class ContentManagementReportRest implements ContentManagementReportApi {

    private final ContentManagementReportController contentManagementReportController;

    @Override
    public ResponseEntity<GetDeleteOperationsReportRespond> getSoftDeleteOperations(String authorization, @Valid Integer page, @Valid Integer size, @Valid DeleteOperationsCriteria criteria) {
        return ResponseEntity.ok(contentManagementReportController.getSoftDeleteOperations(authorization, page, size, criteria));
    }
}
