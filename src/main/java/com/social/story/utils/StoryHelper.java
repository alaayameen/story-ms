package com.social.story.utils;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.social.story.trash.TrashedDataProperties;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.social.core.models.UserInfo;
import com.social.core.utils.JWTUtil;
import com.social.story.consts.StoryConstants;
import com.social.story.convert.VideoConvert;
import com.social.story.data.mongo.models.Story;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * @author ayameen
 *
 */
@Log4j2
@AllArgsConstructor
@Component
public class StoryHelper {

	JWTUtil jwtUtil;
	TrashedDataProperties trashedDataProperties;


	public UserInfo getCurrentUserInfo() {
		return Optional.ofNullable(jwtUtil.getUserInfoFromToken())
				.orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "USER_NOT_FOUND")
						);
	}

	public Pageable pageable(Integer page, Integer size, Sort sort) {
		return PageRequest.of(page - 1, size, sort);
	}
	
	public void verifyAdminUserPrevilidge() {
		UserInfo userInfo = getCurrentUserInfo();
		if (!jwtUtil.isAdminUser()) {
			log.error("Unauthorized access exception is thrown for user {}", userInfo);
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_ACCESS");
		}
	}
	
	public VideoConvert getVideoConvertObject(Story story, String storyBucketName) {
		String videoUrl = story.getMediaDetails().getMediaUrl();
		if (videoUrl == null || videoUrl.trim().isEmpty()) {
			log.error("Video Url is invalid");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "INVALID_VIDEO_URL");
		}

		String outputFolderName = StoryConstants.HLS_FOLDER_NAME;
		String outputVideoType = StoryConstants.HLS_VIDEO_TYPE;
		String outputVideoModifier = StoryConstants.HLS_NAME_MODIFIER;
		
		String outputThumbFolerName= StoryConstants.THUMB_FOLDER_NAME;
		String outputThumbModifier = StoryConstants.THUMB_NAME_MODIFIER;
		String outputThumbType =StoryConstants.THUMB_IMAGE_TYPE;

		String inputS3Uri = videoUrl.replaceAll("^.*.net", "s3://" + storyBucketName);
		// inputS3ObjectName to be used in searching for video existence
		String inputS3ObjectName = inputS3Uri.replaceAll("^.*." + storyBucketName + "[/]", "");
		String outputHttpsUrlPath = videoUrl.substring(0, videoUrl.lastIndexOf('.')).replace("videos", outputFolderName)
				.replace("hls", outputFolderName);
		String outputS3UriPath = outputHttpsUrlPath.replaceAll("^.*.net", "s3://" + storyBucketName);

		String outputS3Uri = outputS3UriPath + outputVideoType; // check this for HLS
		String outputHttpsUrl = outputHttpsUrlPath + outputVideoType;
		String outputS3ObjectName = outputS3Uri.replaceAll("^.*." + storyBucketName + "[/]", "");

		// Define Thumb URLs
		String outputHttpsUrlThumbsPath = outputHttpsUrlPath.replace(outputFolderName, "thumbs");
		String outputS3UriThumbsPath = outputS3UriPath.replace(outputFolderName, outputThumbFolerName);
		String outputThumbsHttpsUrl = outputHttpsUrl.replace(outputFolderName, outputThumbFolerName)
				.replace(outputVideoType, outputThumbModifier + outputThumbType);
		String outputThumbsS3Uri = outputS3ObjectName.replace(outputFolderName, outputThumbFolerName)
				.replace(outputVideoType, outputThumbModifier + outputThumbType);

		// Create VideoConvert object
		VideoConvert videoConvert = new VideoConvert();
		videoConvert.setVideoUrl(videoUrl);
		videoConvert.setBucketName(storyBucketName);
		videoConvert.setInputS3Uri(inputS3Uri);
		videoConvert.setInputS3ObjectName(inputS3ObjectName);
		videoConvert.setOutputFolderName(outputFolderName);
		videoConvert.setOutputVideoType(outputVideoType);
		videoConvert.setOutputVideoModifier(outputVideoModifier);
		videoConvert.setOutputS3Uri(outputS3Uri);
		videoConvert.setOutputS3UriPath(outputS3UriPath);
		videoConvert.setOutputS3ObjectName(outputS3ObjectName);
		videoConvert.setOutputHttpsUrl(outputHttpsUrl);
		videoConvert.setOutputHttpsUrlThumbsPath(outputHttpsUrlThumbsPath);
		videoConvert.setOutputS3UriThumbsPath(outputS3UriThumbsPath);
		videoConvert.setOutputThumbsHttpsUrl(outputThumbsHttpsUrl);
		videoConvert.setOutputThumbsS3Uri(outputThumbsS3Uri);
//		videoConvert.setWidth(story.getMediaDetails().getWidth());
//		videoConvert.setHeight(story.getMediaDetails().getHeight());
		return videoConvert;
	}
	public void isVideoS3ObjectExisting(AmazonS3 s3Client, String mediaBucketName, String s3ObjectNameForInputVideo,
			int maxTries, int s3ObjectWaitTime) {
		int numberOfTries = 0;
		boolean isVideoS3ObjectExisting = s3Client.doesObjectExist(mediaBucketName, s3ObjectNameForInputVideo);
		while (!isVideoS3ObjectExisting) {
			numberOfTries++;
			if (numberOfTries >= maxTries) {
				log.error("Error while trying to wait S3 object to be ready, exceeds max wait time seconds");
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3_OBJECT_FILE_NOT_FOUND");
			}
			try {
				TimeUnit.MILLISECONDS.sleep(s3ObjectWaitTime);
			} catch (Exception ex) {
				log.error("Error while converting video URI", ex);
				ex.printStackTrace();
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ADD_NEW_VIDEO_FAILED");
			}
			isVideoS3ObjectExisting = s3Client.doesObjectExist(mediaBucketName, s3ObjectNameForInputVideo);
		}
		log.info("isVideoS3ObjectExisting is {}", isVideoS3ObjectExisting);
	}

	public ZonedDateTime getCurrentDateTime() {

		return ZonedDateTime.now(ZoneOffset.UTC);
	}
}
