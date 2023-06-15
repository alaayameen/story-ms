package com.social.story.trash.services.impl;

import com.social.story.trash.criteria.CriteriaContext;
import com.social.story.trash.criteria.DeleteOperationsCriteriaBuilder;
import com.social.story.trash.dao.TrashDao;
import com.social.story.trash.mapper.GetTrashDeleteOperationsReportRespondMapper;
import com.social.story.trash.model.Trash;
import com.social.story.trash.services.ContentManagementReportService;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.contentmanagement.DeleteOperationsCriteria;
import com.social.swagger.model.contentmanagement.GetDeleteOperationsReportRespond;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * @author ayameen
 *
 */
@Service
@AllArgsConstructor
@Log4j2
public class ContentManagementReportServiceImpl implements ContentManagementReportService {

    StoryHelper helper;
    TrashDao trashDao;
    GetTrashDeleteOperationsReportRespondMapper getTrashDeleteOperationsReportRespondMapper;


    /**
     * Get hard deleted users report with filter criteria
     *
     * @param authorization logged in user auth token
     * @param page          number
     * @param size          of page
     * @param criteria      details to filter based on
     * @return GetHardDeleteReportRespond
     */
    @Override
    public GetDeleteOperationsReportRespond getSoftDeleteOperations(String authorization, Integer page, Integer size, DeleteOperationsCriteria criteria) {
        Pageable pageable = helper.pageable(page, size, Sort.by("deleted_date").descending());
        CriteriaContext criteriaContext =  new CriteriaContext(new DeleteOperationsCriteriaBuilder());
        Page<Trash> audits = trashDao.findAll(criteriaContext.execute(criteria), pageable);
        return getTrashDeleteOperationsReportRespondMapper.toTarget(audits);
    }
}
