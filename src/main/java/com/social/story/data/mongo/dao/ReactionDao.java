/**
 * 
 */
package com.social.story.data.mongo.dao;

import java.util.List;
import java.util.Optional;

import com.social.story.data.mongo.models.Reaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@AllArgsConstructor
public class ReactionDao {

	private ReactionRepository reactionRepo;

	public Optional<Reaction> getStoryReactionByUserId(String userId, String storyId) {
		log.debug("Get reaction of story Id {} by user {}", storyId, userId);

		return reactionRepo.findByStoryIdAndUserId(storyId, userId);
	}

	public Page<Reaction> getStoryReactions(String storyId, Pageable pageable) {
		log.debug("Get reactions of story Id {}", storyId);

		return reactionRepo.findByStoryId(storyId, pageable);
	}

	public void deleteReaction(Reaction reaction) {
		try {
			log.debug("Deleting reaction {}", reaction);
			reactionRepo.delete(reaction);

		} catch (UncategorizedMongoDbException e) {
			log.error("MONGO_DB_DELETE_REACTION_FAILED while deleting reaction  {}", reaction);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MONGO_DB_DELETE_REACTION_FAILED");
		}
	}

	public void saveReaction(Reaction reaction) {
		try {
			log.debug("Saving reaction {} ", reaction);
			reactionRepo.save(reaction);
		} catch (UncategorizedMongoDbException e) {
			log.error("MONGO_DB_SAVE_REACTION_FAILED while updating reaction state  {}", reaction);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MONGO_DB_SAVE_REACTION_FAILED");
		}
	}

	/**
	 * Deleting all reactions of the story
	 * 
	 * @param storyId
	 */
	public void deleteAllReactionsOfStory(String storyId) {
		try {
			reactionRepo.deleteByStoryId(storyId);

		} catch (UncategorizedMongoDbException e) {
			log.error("MONGO_DB_DELETE_REACTION_FAILED while Deleting reaction of story  {}", storyId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MONGO_DB_DELETE_REACTION_FAILED");
		}
	}

	/**
	 * Deleting all reactons of the story and its replies
	 * 
	 * @param storyId
	 */
	public void deleteAllReactionsOfStorys(List<String> storyIdList) {
		try {

			reactionRepo.deleteByStoryIdIn(storyIdList);

		} catch (UncategorizedMongoDbException e) {
			log.error("MONGO_DB_DELETE_REACTION_FAILED while Deleting reaction of story  {}", storyIdList);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "MONGO_DB_DELETE_REACTION_FAILED");
		}
	}
}
