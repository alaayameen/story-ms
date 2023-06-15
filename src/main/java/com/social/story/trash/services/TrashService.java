package com.social.story.trash.services;


import com.social.story.trash.model.Trash;

import java.util.List;

public interface TrashService {
    void softDeleteStories(List<String> storyIds, String reportId, String name);

    void restoreTrashedStories(List<Trash> trashes, String deleteCommand, String authorization);

    void hardDeleteByUserId(String id);

    void hardDeleteByStoryId(String id);


}
