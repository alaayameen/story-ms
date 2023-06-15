package com.social.story.convert;

import lombok.Data;

/**
 * @author ideek
 *
 */
@Data
public class VideoConvert {

	private String videoUrl;
	private String bucketName;

	private String outputFolderName;
	private String outputVideoModifier;
	private String outputVideoType;

	private String inputS3Uri;
	private String inputS3ObjectName;

	private String outputS3UriPath;
	private String outputS3Uri;
	private String outputS3ObjectName;
	private String outputHttpsUrl;

	private String outputS3UriThumbsPath;
	private String outputHttpsUrlThumbsPath;
	private String outputThumbsHttpsUrl;
	private String outputThumbsS3Uri;

	private int width;
	private int height;

}
