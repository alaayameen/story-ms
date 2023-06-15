package com.social.story.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.social.story.mappers.UserStoriesMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.social.core.models.UserInfo;
import com.social.story.data.mongo.dao.StoryRepository;
import com.social.story.data.mongo.dao.StoryViewRepository;
import com.social.story.data.mongo.dao.impl.StoryViewRepositoryImpl;
import com.social.story.data.mongo.models.Story;
import com.social.story.data.mongo.models.StoryView;
import com.social.story.data.mongo.models.UserStories;
import com.social.story.gateway.FollowApiClient;
import com.social.story.mappers.GetFollowingStoriesRespondMapper;
import com.social.story.mappers.UsersStoriesMapper;
import com.social.story.utils.CacheManager;
import com.social.story.utils.StoryHelper;
import com.social.swagger.called.follow.model.GetFollowingStatusRequest;
import com.social.swagger.called.follow.model.GetFollowingStatusRespond;
import com.social.swagger.called.follow.model.GetFollowingStatusRespondInner;
import com.social.swagger.called.user.model.PublicUserInfo;
import com.social.swagger.model.story.GetFollowingStoriesRespond;
import com.social.swagger.model.story.RetrieveFollowingsStoriesRespond;

import lombok.extern.log4j.Log4j2;

@Log4j2
@ExtendWith(MockitoExtension.class)
public class AllStoriesServiceImplTest {
	private String storyId = "111";
	private String storyViewId = "112";
	private String userId = "user1";
	private String followingUserId = "fuser1";

	private Story story;
	private StoryView storyView;
	private List<String> usersList = Arrays.asList(new String[] { userId });
	private List<String> followingUsersList = Arrays.asList(new String[] { followingUserId });

	@Mock
	private StoryRepository storyRepository;

	@Mock
	private StoryViewRepository storyViewRepository;

	@Mock
	private StoryHelper helper;

	@Mock
	private CacheManager cacheManager;

	@Mock
	private FollowApiClient followApiClient;

	@Mock
	private StoryViewRepositoryImpl storyViewRepositoryImpl;

	@Mock
	private GetFollowingStoriesRespondMapper getFollowingStoriesRespondMapper;

	@Mock
	private UsersStoriesMapper usersStoriesMapper;

	@Mock
	private UserStoriesMapper userStoriesMapper;

	@InjectMocks
	private AllStoriesServiceImpl allStoriesServiceImpl;
	private String auth = "authTest";

	@BeforeEach
	public void setUp() {
		log.info("setUp invoked!");
		// Setup data to be used in test
		story = new Story();
		story.setId(storyId);
		story.setUsersSeenNumber(0);

		storyView = new StoryView();
		storyView.setStoryId(storyId);
		storyView.setId(storyViewId);

		UserInfo userInfo = new UserInfo();
		userInfo.setId(userId);

		when(helper.getCurrentUserInfo()).thenReturn(userInfo);
	}

	@AfterEach
	public void tearDown() {
		log.info("tearDown invoked!");
		reset(storyRepository);
		reset(storyViewRepository);
		reset(helper);
		reset(cacheManager);
		reset(followApiClient);
		reset(storyViewRepositoryImpl);
		reset(getFollowingStoriesRespondMapper);
		reset(usersStoriesMapper);
		reset(userStoriesMapper);
	}

	@Test
	public void testMarkStoryAsViewedStoryViewExists() {
		log.info("testMarkStoryAsViewedStoryViewExists started!");

		when(storyRepository.findById(Mockito.eq(storyId))).thenReturn(Optional.of(story));
		when(storyViewRepository.findByStoryIdAndUserId(Mockito.eq(storyId), Mockito.eq(userId)))
				.thenReturn(Optional.of(storyView));

		allStoriesServiceImpl.markStoryAsViewed(userId, storyId);

		Mockito.verify(storyViewRepository, Mockito.never()).save(Mockito.any());
		Mockito.verify(storyRepository, Mockito.never()).save(Mockito.any());
	}

	@Test
	public void testMarkStoryAsViewedStoryViewNotExists() {
		log.info("testMarkStoryAsViewedStoryViewNotExists started!");

		when(storyRepository.findById(Mockito.eq(storyId))).thenReturn(Optional.of(story));
		when(storyViewRepository.findByStoryIdAndUserId(Mockito.eq(storyId), Mockito.eq(userId)))
				.thenReturn(Optional.empty());

		allStoriesServiceImpl.markStoryAsViewed(userId, storyId);

		assertEquals(story.getUsersSeenNumber().intValue(), 1);

		Mockito.verify(storyViewRepository, Mockito.times(1)).save(Mockito.any());
		Mockito.verify(storyRepository, Mockito.times(1)).save(Mockito.any());
	}

	@Test
	public void testGetFollowingStories() {
		log.info("testGetFollowingStories started!");
		Map<String, PublicUserInfo> usersMap = createPublicUserInfoMap(userId);
		GetFollowingStatusRespond followingStatusRespond = createGetFollowingStatusRespond();

		when(cacheManager.getUsersInfoReport(auth, followingUsersList)).thenReturn(usersMap);
		when(followApiClient.getFollowingStatus(Mockito.anyString(), Mockito.any(GetFollowingStatusRequest.class)))
				.thenReturn(followingStatusRespond);
		when(helper.pageable(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Sort.class))).thenCallRealMethod();

		List<Story> storiesList = new ArrayList<>();
		storiesList.add(story);
		when(storyViewRepositoryImpl.followingStories(Mockito.anyList(), Mockito.anyString(),
				Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(storiesList));
		when(getFollowingStoriesRespondMapper.toTarget(Mockito.any(Page.class)))
				.thenReturn(createFollowingStoriesRespond(new PageImpl<>(storiesList)));

		GetFollowingStoriesRespond getFollowingStoriesRespond = allStoriesServiceImpl.getFollowingStories(auth,
				followingUserId, 1, 10);

		assertNotNull(getFollowingStoriesRespond);
	}

	@Test
	public void testGetFollowingsStories() {
		log.info("testGetFollowingsStories started!");
		Map<String, PublicUserInfo> usersMap = createPublicUserInfoMap(followingUserId);
		when(followApiClient.getUserFollowingsIds(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
				.thenReturn(followingUsersList);
		when(cacheManager.getUsersInfoReport(auth, followingUsersList)).thenReturn(usersMap);
		when(helper.pageable(Mockito.anyInt(), Mockito.anyInt(), Mockito.any(Sort.class))).thenCallRealMethod();

		List<UserStories> userStoriesList = createUserStoriesList();
		when(storyViewRepositoryImpl.followingStoriesGroupByUser(Mockito.anyList(), Mockito.anyString(),
				Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(userStoriesList));
		when(userStoriesMapper.toTargets(userStoriesList)).thenCallRealMethod();

		RetrieveFollowingsStoriesRespond retrieveFollowingsStoriesRespond = allStoriesServiceImpl
				.getFollowingsStories(auth, 1, 10);

		assertNotNull(retrieveFollowingsStoriesRespond);
	}

	private List<UserStories> createUserStoriesList() {
		List<UserStories> userStoriesList = new ArrayList<>();
		List<Story> storiesList = new ArrayList<>();
		storiesList.add(story);
		UserStories userStories = new UserStories();
		userStories.setId(userId);
		userStories.setPublicUserInfo(new PublicUserInfo());
		userStories.setStories(storiesList);
		userStoriesList.add(userStories);
		return userStoriesList;
	}

	private GetFollowingStatusRespond createGetFollowingStatusRespond() {
		GetFollowingStatusRespond followingStatusRespond = new GetFollowingStatusRespond();
		GetFollowingStatusRespondInner followingStatusRespondInner = new GetFollowingStatusRespondInner();
		followingStatusRespondInner.setFollowing(Boolean.TRUE);
		followingStatusRespond.add(followingStatusRespondInner);
		return followingStatusRespond;
	}

	private Map<String, PublicUserInfo> createPublicUserInfoMap(String userId) {
		PublicUserInfo publicUserInfo = new PublicUserInfo();
		publicUserInfo.setUserId(userId);
		publicUserInfo.setUserName("userName");
		Map<String, PublicUserInfo> usersMap = new HashMap<>();
		usersMap.put(userId, publicUserInfo);
		return usersMap;
	}

	private GetFollowingStoriesRespond createFollowingStoriesRespond(Page<Story> source) {
		GetFollowingStoriesRespond getFollowingStoriesRespond = new GetFollowingStoriesRespond();
		getFollowingStoriesRespond.setPageNumber(source.getNumber() + 1);
		getFollowingStoriesRespond.setPageSize(source.getSize());
		getFollowingStoriesRespond.setTotalPages(source.getTotalPages());
		getFollowingStoriesRespond.setTotalElements(source.getTotalElements());
		return getFollowingStoriesRespond;
	}

}
