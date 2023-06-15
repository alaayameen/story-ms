package com.social.story.trash.services.impl;

import com.google.common.collect.Lists;
import com.social.core.models.UserInfo;
import com.social.story.data.mongo.dao.StoryDao;
import com.social.story.data.mongo.dao.StoryViewDao;
import com.social.story.data.mongo.models.Story;
import com.social.story.data.mongo.models.StoryView;
import com.social.story.trash.dao.TrashDao;
import com.social.story.trash.enums.ContentCommand;
import com.social.story.trash.mapper.TrashedStoryMapper;
import com.social.story.trash.mapper.TrashedStoryViewMapper;
import com.social.story.trash.model.Trash;
import com.social.story.trash.services.TrashService;
import com.social.story.trash.utils.Constants;
import com.social.story.trash.utils.TrashHelper;
import com.social.story.utils.StoryHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author ayameen
 */
@Service
@AllArgsConstructor
public class TrashServiceImpl implements TrashService {

    StoryDao storyDao;
    StoryViewDao storyViewDao;
    TrashDao trashDao;
    TrashedStoryMapper trashedStoryMapper;
    TrashHelper trashHelper;
    TrashedStoryViewMapper trashStoryViewMapper;
    StoryHelper helper;

    @Override
    public void softDeleteStories(List<String> storyIds, String reportId, String command) {
        UserInfo userInfo = helper.getCurrentUserInfo();
        List<List<String>> chunks = Lists.partition(storyIds, Constants.FETCH_BATCH_SIZE);
        chunks.forEach(chunk -> {
            List<Story> stories = storyDao.findByIdIn(chunk);
            deleteStoryViewByStoryIds(chunk, command, reportId, userInfo.getId());

            List<Trash> trashes = stories.stream().map(post -> {
                Trash trash = trashedStoryMapper.toTrash(post, command);
                trash.setReportingId(reportId);
                trash.setDeletedBy(userInfo.getId());
                return trash;
            }).collect(Collectors.toList());

            populateExpireAt(trashes, command, trashHelper.getExpireAtInDays());
            trashDao.saveAll(trashes);
            storyDao.deleteAll(stories);
        });
    }

    @Override
    public void restoreTrashedStories(List<Trash> trashes, String deleteCommand, String authorization) {
        if (trashes == null || trashes.isEmpty()) {
            return;
        }

        List<List<Trash>> chunks = Lists.partition(trashes, Constants.FETCH_BATCH_SIZE);
        chunks.forEach(chunk -> {
            List<String> storyIds =  chunk.stream().map(Trash::getEntityId).collect(Collectors.toList());
            restoreStoryViewByStoryIds(storyIds, deleteCommand);
            List<Story> stories = chunk.stream().map(trash -> trashedStoryMapper.toStory(trash)).collect(Collectors.toList());
            storyDao.insertAllIgnoreAuditing(stories);
            trashDao.deleteAll(chunk);
        });
    }

    public void restoreStoryViewByStoryIds(List<String> storyIds, String deletedCommand) {
        List<List<String>> chunks = Lists.partition(storyIds, Constants.FETCH_BATCH_SIZE);
        chunks.forEach(chunk -> {
            List<Trash> trashList = trashDao.findByParentIdInAndCommandAndEntityName(chunk, deletedCommand, Constants.CONTENT_TYPE.STORY_VIEW.name());
            if (trashList == null || trashList.isEmpty()) {
                return;
            }
            List<StoryView> storyViews = trashList.stream().map(trash -> trashStoryViewMapper.toStoryView(trash)).collect(Collectors.toList());
            storyViewDao.insertAllIgnoreAuditing(storyViews);
            trashDao.deleteAll(trashList);
        });
    }

    @Override
    public void hardDeleteByUserId(String userId) {
        List<String> commentIds =  storyDao.findIdByUserId(userId);
        List<List<String>> chunks = Lists.partition(commentIds, Constants.FETCH_BATCH_SIZE);
        chunks.forEach(chunk -> {
            storyDao.deleteByIdIn(chunk);
            storyViewDao.deleteByStoryIdIn(chunk);
        });
    }

    @Override
    public void hardDeleteByStoryId(String id) {
        storyDao.deleteByIdIn(Collections.singletonList(id));
        storyViewDao.deleteByStoryIdIn(Collections.singletonList(id));
    }

    public void deleteStoryViewByStoryIds(List<String> storyIds, String command, String reportId, String deletedBy) {
        List<List<String>> chunks = Lists.partition(storyIds, Constants.FETCH_BATCH_SIZE);
        chunks.forEach(chunk -> {
            List<StoryView> storyViews = storyViewDao.findByStoryIdIn(chunk);
            List<Trash> trashedLikes = storyViews.stream().map(like -> {
                Trash trash = trashStoryViewMapper.toTrash(like, command, like.getStoryId());
                trash.setReportingId(reportId);
                trash.setDeletedBy(deletedBy);
                return trash;
            }).collect(Collectors.toList());
            trashDao.saveAll(trashedLikes);
            storyViewDao.deleteAll(storyViews);
        });
    }

    private void populateExpireAt(List<Trash> trashedPosts, String command, Integer expireAtInDays) {
        if (command.equals(ContentCommand.DELETE_ACCOUNT_SOFT_DELETE.name())) {
            trashedPosts.forEach(trash -> trash.setExpireAt(LocalDateTime.now().plusDays(expireAtInDays)));
        }
    }
}
