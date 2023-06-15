package com.social.story.trash.services.impl;

import com.social.story.trash.operatios.ContentCommandExecutor;
import com.social.story.trash.operatios.ContentCommandFactory;
import com.social.story.trash.services.ContentManagementService;
import com.social.story.trash.utils.Constants;
import com.social.swagger.model.contentmanagement.ContentManagementRequest;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * @author ayameen
 *
 */
@Service
@AllArgsConstructor
@Log4j2
public class ContentManagementServiceImpl implements ContentManagementService {

    private final ContentCommandFactory contentCommandFactory;

    /**
     * Take an action (soft delete, restore, hard delete, ban, un ban), action sources(direct by admin, report, delete account)
     * on content(Post, story, video, comment, reply and user) by content id list
     *
     * @param authorization            logged in user
     * @param contentManagementRequest request body
     */
    @Override
    public void manageContentById(String authorization, ContentManagementRequest contentManagementRequest) {
        ContentCommandExecutor contentCommandExecutor =  new ContentCommandExecutor(
                contentCommandFactory.getCommentCommand(contentManagementRequest,Constants.CONTENT_TYPE.STORY.name(), authorization),
                Constants.ID_TYPE.valueOf(contentManagementRequest.getIdType().name()));
        contentCommandExecutor.execute();
    }
}
