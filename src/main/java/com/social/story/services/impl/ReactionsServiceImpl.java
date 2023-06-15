/**
 * 
 */
package com.social.story.services.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.social.story.services.ReactionsService;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.social.story.data.mongo.dao.ReactionDao;
import com.social.story.data.mongo.models.Reaction;
import com.social.story.data.mongo.models.ReactionType;
import com.social.story.utils.StoryHelper;
import com.social.swagger.model.story.StoryReactionInfo;
import com.social.swagger.model.story.StoryReactionsResponse;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
@AllArgsConstructor
public class ReactionsServiceImpl implements ReactionsService {

	private ReactionDao reactionDao;

    private StoryHelper helper;

	@Override
	public void saveStoryReaction(String userId, String storyId, ReactionType reactionType) {
		log.debug("React on storyId {} by userId {} with reaction {} ", storyId, userId, reactionType);

		Reaction reaction = reactionDao.getStoryReactionByUserId(userId, storyId)
				.orElseGet(() -> Reaction.builder()
						.storyId(storyId)
						.userId(userId)
						.reactionType(reactionType)
						.build());

		toggleReaction(reaction);
	}

	private void toggleReaction(Reaction reaction) {
		log.debug("Changing reaction type of reaction {}", reaction);
		if (Objects.nonNull(reaction.getId())) {
			reactionDao.deleteReaction(reaction);
		} else {
			reactionDao.saveReaction(reaction);
		}
	}

	@Override
	public StoryReactionsResponse getStoryReactions(String storyId, Integer page, Integer size) {
		if (!ObjectId.isValid(storyId)) {
			log.error("StoryId is invalid {}", storyId);
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "STORY_ID_INVALID");
		}
		Pageable pageable = helper.pageable(page, size, Sort.by("createdDt").descending());
		Page<Reaction> reactionsPage = reactionDao.getStoryReactions(storyId, pageable);

		StoryReactionsResponse storyReactionsResponse = new StoryReactionsResponse();
		if (reactionsPage.hasContent()) {
			List<StoryReactionInfo> storyReactionInfoList = reactionsPage.stream().map(re -> {
				StoryReactionInfo storyReactionInfo = new StoryReactionInfo();
				storyReactionInfo.setReactionType(re.getReactionType().toString());
				storyReactionInfo.setStoryId(re.getStoryId());
				storyReactionInfo.setUserId(re.getUserId());
				return storyReactionInfo;
			}).collect(Collectors.toList());

			storyReactionsResponse.setData(storyReactionInfoList);
		}
		storyReactionsResponse.setPageNumber(page);
		storyReactionsResponse.setPageSize(size);
		storyReactionsResponse.setTotalElements(reactionsPage.getTotalElements());
		storyReactionsResponse.setTotalPages(reactionsPage.getTotalPages());

		return storyReactionsResponse;
	}
}
