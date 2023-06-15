package com.social.story.config;

import com.social.core.models.UserInfo;
import com.social.story.utils.StoryHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

/**
 * @author ayameen
 *
 */
@Component
@AllArgsConstructor
public class OpTubeAuditing implements AuditorAware<String> {
	
	StoryHelper helper;
	
	@Override
	public Optional<String> getCurrentAuditor() {
		
		String username = "OpTube";
		
		UserInfo user =   helper.getCurrentUserInfo();
		if(Objects.nonNull(user)) {
			username = user.getId();
		}
		
		return Optional.of(username);
	}

}
