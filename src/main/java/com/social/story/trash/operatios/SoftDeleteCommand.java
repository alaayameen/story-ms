package com.social.story.trash.operatios;

import com.social.story.data.mongo.dao.StoryDao;
import com.social.story.trash.services.TrashService;
import com.social.story.trash.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author ayameen
 *
 */
@Service
@Log4j2
public class SoftDeleteCommand extends Command {

    @Autowired
    StoryDao storyDao;

    @Autowired
    TrashService trashService;

    @Override
    public void execute(Constants.ID_TYPE idType) {
        log.debug("Execute Command: {}, ID Type: {}, Id: {}, Report Id: {}",
                contentManagementRequest.getContentCommand(),
                idType, contentManagementRequest.getId(), contentManagementRequest.getReportId());
        if (idType == Constants.ID_TYPE.USER_ID) {
            softDeleteByUserId();
        } else if (idType == Constants.ID_TYPE.CONTENT_ID) {
            deleteByContentId();
        }
    }

    void softDeleteByUserId() {
        List<String> storyIds = storyDao.findIdByUserId(contentManagementRequest.getId());
        trashService.softDeleteStories(storyIds, contentManagementRequest.getReportId(), contentManagementRequest.getContentCommand().name());
    }

    void deleteByContentId() {
            trashService.softDeleteStories(
                    Collections.singletonList(contentManagementRequest.getId()),
                    contentManagementRequest.getReportId(), contentManagementRequest.getContentCommand().name());

    }
}
