package com.social.story.trash.operatios;


import com.social.story.data.mongo.dao.StoryDao;
import com.social.story.trash.services.TrashService;
import com.social.story.trash.utils.Constants;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ayameen
 *
 */
@Service
@Log4j2
public class BanUserCommand extends Command {

    @Autowired
    StoryDao storyDao;

    @Autowired
    TrashService trashService;

    @Override
    public void execute(Constants.ID_TYPE idType) {
        log.debug("Execute Command: {}, ID Type: {}, Id: {}, Report Id: {}", contentManagementRequest.getContentCommand(),
                idType, contentManagementRequest.getId(), contentManagementRequest.getReportId());
        if (idType.equals(Constants.ID_TYPE.USER_ID)) {
            banUsersContent();
        }
    }

    void banUsersContent() {
        List<String> storyIds = storyDao.findIdByUserId(contentManagementRequest.getId());
        trashService.softDeleteStories(storyIds, contentManagementRequest.getReportId(), contentManagementRequest.getContentCommand().name());
    }
}
