package com.social.story.services.impl;

import java.net.URL;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

@Service
public class FileUploadService {

	@Autowired
	private AmazonS3 s3Client;

	@Value("${document.bucket-name}")
	private String storyBucketName;

	@Value("${url.expiry}")
	private Long urlExpiryAfter;

	public String generateSignedUrl(String fileKey) throws InstantiationException, IllegalAccessException {

		Date expiryDate = new Date(System.currentTimeMillis() + urlExpiryAfter);
		System.out.println("storyBucketName is " + storyBucketName);
		GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(storyBucketName, fileKey, HttpMethod.PUT);
		request.setExpiration(expiryDate);
		request.addRequestParameter("x-amz-acl", "public-read");
		URL signedUrl = s3Client.generatePresignedUrl(request);
		return signedUrl.toString();
	}

}
