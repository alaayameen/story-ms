package com.social.story.services;

import com.social.story.gateway.FollowApiClient;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author ayameen
 */
@Service
@AllArgsConstructor
public class FollowService {
	
	private final FollowApiClient followApiClient;
	
	@Async
	public CompletableFuture<List<String>> getUserFollowingsIdsAsync(String authorization, Integer page, Integer size){
		List<String> response= followApiClient.getUserFollowingsIds(authorization, page, size);
		return CompletableFuture.completedFuture(response);
	}
	
	public List<String> getUserFollowingsIds(String authorization, Integer page, Integer size){
		return followApiClient.getUserFollowingsIds(authorization, page, size);
	}
}
