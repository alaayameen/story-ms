package com.social.story.data.mongo.dao.impl;

import com.social.story.data.mongo.models.Story;
import com.social.story.data.mongo.models.UserStories;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;
import static org.springframework.data.mongodb.core.query.Criteria.where;

@Repository
@AllArgsConstructor
public class StoryViewRepositoryImpl {

	MongoTemplate mongoTemplate;

	public void addViewerToStory(String userId, String storyId) {

		Update update = new Update();
		update.addToSet("viewers", userId);
		Criteria criteria = Criteria.where("storyId").is(storyId);
		mongoTemplate.updateFirst(Query.query(criteria), update, "story_views");
	}

	/**
	 * Return page of stories for following users added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
	 *
	 * @param followingUsersId list of following users to retrieve their stories
	 * @param loggedInUserID the logged in user needs to fetch the stories with seen(by him) status
	 * @param pageable contins pagination and sort info
	 * @return page of stories
	 */
	public Page<Story> followingStories(List<String> followingUsersId, String loggedInUserID, Pageable pageable) {

		List<ObjectId> followingsIds = new ArrayList<>();
		if (followingUsersId != null) {
			followingUsersId.forEach(id -> followingsIds.add(new ObjectId(id)));
		}

		/*
		 * Projection to return story attributes with adding new attribute "seen" (check if logged in user exists in seenByIds list
		 * seen value is true|false
		 */
		ProjectionOperation project = project("id", "caption", "userId", "mediaDetails", "hashtags", "createdDt",
				"usersSeenNumber").and(ArrayOperators.In.arrayOf("$seenByIds").containsValue(loggedInUserID))
				.as("seen");

		/*
		 * Sort and pagination aggregations
		 */
		SortOperation sortOperation = sort(pageable.getSort());
		SkipOperation skipOperation = skip((long) pageable.getPageNumber() * pageable.getPageSize());
		LimitOperation limitOperation = limit(pageable.getPageSize());


		Aggregation aggregation = newAggregation(
				match(where("userId").in(followingsIds)),
				match(where("createdDt").gt(LocalDateTime.now().minusHours(24))),
				project,
				sortOperation,
				skipOperation,
				limitOperation);

		List<Story> list = mongoTemplate.aggregate(
				aggregation, "story", Story.class).getMappedResults();

		return new PageImpl<>(list, pageable, list.size());
	}


	/**
	 * Return page of user stories for following users added during the last 24 hours, It will return not seen stories first with indication for not seen sorted by created date LIFO
	 * all of that will be grouped by following user id, user stories entry with latest not seen story will be first
	 *
	 * @param followingUsersId list of following users to retrieve their stories
	 * @param loggedInUserID the logged in user needs to fetch the stories with seen(by him) status
	 * @param pageable contins pagination and sort info
	 * @return page of user stories
	 */
	public Page<UserStories> followingStoriesGroupByUser(List<String> followingUsersId, String loggedInUserID, Pageable pageable) {

		List<ObjectId> followingsIds = new ArrayList<>();
		if (followingUsersId != null) {
			followingUsersId.forEach(id -> followingsIds.add(new ObjectId(id)));
		}

		/*
		 * Projection to return story attributes with adding new attribute "seen" (check if logged in user exists in seenByIds list
		 * seen value is true|false
		 */
		ProjectionOperation project =
				project("id", "caption", "userId", "mediaDetails", "hashtags", "createdDt")
						.and(ArrayOperators.In.arrayOf("$seenByIds")
								.containsValue(loggedInUserID)).as("seen");


		/*
		 * Sort and pagination aggregations
		 */
		SortOperation sortOperation = sort(pageable.getSort());
		SortOperation sortOperationAfterGroup = sort(Sort.by("stories.seen").ascending().and(Sort.by("maxDate").descending()));

		/*
		 * TODO: We should find the maxDate for not seen stories grouped by the userId
		 */
		GroupOperation groupOperation = group("userId").push("$$ROOT").as("stories").max("createdDt").as("maxDate");
		SkipOperation skipOperation = skip((long) pageable.getPageNumber() * pageable.getPageSize());
		LimitOperation limitOperation = limit(pageable.getPageSize());

		Aggregation aggregation = newAggregation(
				match(where("userId").in(followingsIds)),
				match(where("createdDt").gt(LocalDateTime.now().minusHours(24))),
				project,
				sortOperation,
				groupOperation,
				sortOperationAfterGroup,
				skipOperation,
				limitOperation);

		List<UserStories> list = mongoTemplate.aggregate(
				aggregation, "story", UserStories.class).getMappedResults();

		return new PageImpl<>(list, pageable, list.size());
	}
}
