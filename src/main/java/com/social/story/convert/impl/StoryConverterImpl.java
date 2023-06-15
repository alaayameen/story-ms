/**
 * 
 */
package com.social.story.convert.impl;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.social.story.data.mongo.models.Story;
import com.social.story.utils.StoryHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ResponseStatusException;

import com.amazonaws.services.s3.AmazonS3;
import com.social.story.convert.StoryConverter;
import com.social.story.convert.VideoConvert;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.mediaconvert.model.AacAudioDescriptionBroadcasterMix;
import software.amazon.awssdk.services.mediaconvert.model.AacCodecProfile;
import software.amazon.awssdk.services.mediaconvert.model.AacCodingMode;
import software.amazon.awssdk.services.mediaconvert.model.AacRateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.AacRawFormat;
import software.amazon.awssdk.services.mediaconvert.model.AacSettings;
import software.amazon.awssdk.services.mediaconvert.model.AacSpecification;
import software.amazon.awssdk.services.mediaconvert.model.AfdSignaling;
import software.amazon.awssdk.services.mediaconvert.model.AntiAlias;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodec;
import software.amazon.awssdk.services.mediaconvert.model.AudioCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.AudioDefaultSelection;
import software.amazon.awssdk.services.mediaconvert.model.AudioDescription;
import software.amazon.awssdk.services.mediaconvert.model.AudioLanguageCodeControl;
import software.amazon.awssdk.services.mediaconvert.model.AudioSelector;
import software.amazon.awssdk.services.mediaconvert.model.AudioTypeControl;
import software.amazon.awssdk.services.mediaconvert.model.ColorMetadata;
import software.amazon.awssdk.services.mediaconvert.model.ColorSpace;
import software.amazon.awssdk.services.mediaconvert.model.ContainerSettings;
import software.amazon.awssdk.services.mediaconvert.model.ContainerType;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.CreateJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsRequest;
import software.amazon.awssdk.services.mediaconvert.model.DescribeEndpointsResponse;
import software.amazon.awssdk.services.mediaconvert.model.DropFrameTimecode;
import software.amazon.awssdk.services.mediaconvert.model.FileGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.FrameCaptureSettings;
import software.amazon.awssdk.services.mediaconvert.model.GetJobRequest;
import software.amazon.awssdk.services.mediaconvert.model.GetJobResponse;
import software.amazon.awssdk.services.mediaconvert.model.H264AdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264CodecLevel;
import software.amazon.awssdk.services.mediaconvert.model.H264CodecProfile;
import software.amazon.awssdk.services.mediaconvert.model.H264DynamicSubGop;
import software.amazon.awssdk.services.mediaconvert.model.H264EntropyEncoding;
import software.amazon.awssdk.services.mediaconvert.model.H264FieldEncoding;
import software.amazon.awssdk.services.mediaconvert.model.H264FlickerAdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264FramerateControl;
import software.amazon.awssdk.services.mediaconvert.model.H264FramerateConversionAlgorithm;
import software.amazon.awssdk.services.mediaconvert.model.H264GopBReference;
import software.amazon.awssdk.services.mediaconvert.model.H264GopSizeUnits;
import software.amazon.awssdk.services.mediaconvert.model.H264InterlaceMode;
import software.amazon.awssdk.services.mediaconvert.model.H264ParControl;
import software.amazon.awssdk.services.mediaconvert.model.H264QualityTuningLevel;
import software.amazon.awssdk.services.mediaconvert.model.H264QvbrSettings;
import software.amazon.awssdk.services.mediaconvert.model.H264RateControlMode;
import software.amazon.awssdk.services.mediaconvert.model.H264RepeatPps;
import software.amazon.awssdk.services.mediaconvert.model.H264SceneChangeDetect;
import software.amazon.awssdk.services.mediaconvert.model.H264Settings;
import software.amazon.awssdk.services.mediaconvert.model.H264SlowPal;
import software.amazon.awssdk.services.mediaconvert.model.H264SpatialAdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264Syntax;
import software.amazon.awssdk.services.mediaconvert.model.H264Telecine;
import software.amazon.awssdk.services.mediaconvert.model.H264TemporalAdaptiveQuantization;
import software.amazon.awssdk.services.mediaconvert.model.H264UnregisteredSeiTimecode;
import software.amazon.awssdk.services.mediaconvert.model.HlsCaptionLanguageSetting;
import software.amazon.awssdk.services.mediaconvert.model.HlsClientCache;
import software.amazon.awssdk.services.mediaconvert.model.HlsCodecSpecification;
import software.amazon.awssdk.services.mediaconvert.model.HlsDirectoryStructure;
import software.amazon.awssdk.services.mediaconvert.model.HlsGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.HlsIFrameOnlyManifest;
import software.amazon.awssdk.services.mediaconvert.model.HlsManifestCompression;
import software.amazon.awssdk.services.mediaconvert.model.HlsManifestDurationFormat;
import software.amazon.awssdk.services.mediaconvert.model.HlsOutputSelection;
import software.amazon.awssdk.services.mediaconvert.model.HlsProgramDateTime;
import software.amazon.awssdk.services.mediaconvert.model.HlsSegmentControl;
import software.amazon.awssdk.services.mediaconvert.model.HlsSettings;
import software.amazon.awssdk.services.mediaconvert.model.HlsStreamInfResolution;
import software.amazon.awssdk.services.mediaconvert.model.HlsTimedMetadataId3Frame;
import software.amazon.awssdk.services.mediaconvert.model.Input;
import software.amazon.awssdk.services.mediaconvert.model.InputDeblockFilter;
import software.amazon.awssdk.services.mediaconvert.model.InputDenoiseFilter;
import software.amazon.awssdk.services.mediaconvert.model.InputFilterEnable;
import software.amazon.awssdk.services.mediaconvert.model.InputPsiControl;
import software.amazon.awssdk.services.mediaconvert.model.InputRotate;
import software.amazon.awssdk.services.mediaconvert.model.InputTimecodeSource;
import software.amazon.awssdk.services.mediaconvert.model.Job;
import software.amazon.awssdk.services.mediaconvert.model.JobSettings;
import software.amazon.awssdk.services.mediaconvert.model.JobStatus;
import software.amazon.awssdk.services.mediaconvert.model.M3u8NielsenId3;
import software.amazon.awssdk.services.mediaconvert.model.M3u8PcrControl;
import software.amazon.awssdk.services.mediaconvert.model.M3u8Scte35Source;
import software.amazon.awssdk.services.mediaconvert.model.M3u8Settings;
import software.amazon.awssdk.services.mediaconvert.model.MediaConvertException;
import software.amazon.awssdk.services.mediaconvert.model.Output;
import software.amazon.awssdk.services.mediaconvert.model.OutputDetail;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroup;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupDetail;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupSettings;
import software.amazon.awssdk.services.mediaconvert.model.OutputGroupType;
import software.amazon.awssdk.services.mediaconvert.model.OutputSettings;
import software.amazon.awssdk.services.mediaconvert.model.RespondToAfd;
import software.amazon.awssdk.services.mediaconvert.model.ScalingBehavior;
import software.amazon.awssdk.services.mediaconvert.model.TimedMetadata;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodec;
import software.amazon.awssdk.services.mediaconvert.model.VideoCodecSettings;
import software.amazon.awssdk.services.mediaconvert.model.VideoDescription;
import software.amazon.awssdk.services.mediaconvert.model.VideoSelector;
import software.amazon.awssdk.services.mediaconvert.model.VideoTimecodeInsertion;

/**
 * @author ideek
 *
 */
@Component
@Log4j2
public class StoryConverterImpl implements StoryConverter {

	@Value("${cloud.aws.region.static}")
	private String region;

	@Value("${document.bucket-name}")
	private String storyBucketName;

	@Value("${cloud.aws.mediaconvert.role}")
	private String mediaConvertRole;

	@Autowired
	private AmazonS3 s3Client;

	@Autowired
    StoryHelper storyHelper;

	private static int MAX_TRIES = 5;

	private static int JOB_WAIT_SECONDS = 5;
	
	private static int FILE_UPLOAD_MAX_TRIES = 40;
	private static int FILE_UPLOAD_WAIT_TIME_MILLISECONDS = 500;

	private static int SET_DIMENSIONS_MAX_TRIES = 20;
	private static long SET_DIMENSIONS_WAIT_TIME_SECONDS = 2;

	private MediaConvertClient mediaConvertClient;

	@PostConstruct
	public void initialize() {
		log.debug("Initialize story convert client");
		mediaConvertClient = MediaConvertClient.builder().region(Region.of(region)).build();

		DescribeEndpointsResponse res = mediaConvertClient
				.describeEndpoints(DescribeEndpointsRequest.builder().maxResults(20).build());

		if (res.endpoints().size() <= 0) {
			log.error("Error finidng end point to convert video");
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ADD_NEW_VIDEO_FAILED");
		}

		String endpointURL = res.endpoints().get(0).url();

		mediaConvertClient = MediaConvertClient.builder().region(Region.of(region))
				.endpointOverride(URI.create(endpointURL)).build();

	}

	/**
	 * This method converts mp4 video to hls.
	 */
	@Override
	public Story convertVideoToHls(Story story) {

		VideoConvert videoConvert = storyHelper.getVideoConvertObject(story, storyBucketName);
		String storyUrl = videoConvert.getVideoUrl();

		log.info("Verifying the story video file is existing on S3 before starting converting");
		storyHelper.isVideoS3ObjectExisting(s3Client, storyBucketName, videoConvert.getInputS3ObjectName(),
				FILE_UPLOAD_MAX_TRIES, FILE_UPLOAD_WAIT_TIME_MILLISECONDS);

		log.info("Converting a story video with url {} onto HLS formal", storyUrl);
		try {
			String jobId = createMediaJobToCreateHls(mediaConvertClient, videoConvert, false);
			if (ObjectUtils.isEmpty(jobId)) {
				log.error("Error while creating HLS from story video with id{}", story.getId());
				throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CONVERT_VIDEO_FAILED");
			}

			log.info("Verifying Job status for job with Id {} is completed", jobId);
			verifyJob(mediaConvertClient, jobId, 1, JOB_WAIT_SECONDS);

			log.info("Verifying the output file {} is uploaded to S3", videoConvert.getOutputS3ObjectName());
			storyHelper.isVideoS3ObjectExisting(s3Client, storyBucketName, videoConvert.getOutputS3ObjectName(),
					FILE_UPLOAD_MAX_TRIES, FILE_UPLOAD_WAIT_TIME_MILLISECONDS);

//			log.info("Setting dimensions for story video with id {}", story.getId());
//			setVideoDimensions(jobId, story, mediaConvertClient);

		} catch (Exception e) {
			log.error("Error while converting story video with id {} , error details {}", story.getId(), e);
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CONVERT_VIDEO_FAILED");
		}

		story.getMediaDetails().setMediaUrl(videoConvert.getOutputHttpsUrl());
		story.getMediaDetails().setThumb(videoConvert.getOutputThumbsHttpsUrl());
		return story;
	}

	public String createMediaJobToCreateHls(MediaConvertClient emc, VideoConvert videoConvert, boolean withSound) {

		String completeOutputS3VideoPathWithoutType = videoConvert.getOutputS3UriPath();
		String s3UriThumbsPath = videoConvert.getOutputS3UriThumbsPath();
		String nameModifier = videoConvert.getOutputVideoModifier();
		String fileInput = videoConvert.getInputS3Uri();

		log.info("Creating a job for converting file: {} with sound convert {} to fileOutput {} ", fileInput, withSound,
				completeOutputS3VideoPathWithoutType);
		try {

			// Create AudioSelector
			Map<String, AudioSelector> audioSelectors = new HashMap<String, AudioSelector>();
			audioSelectors.put("Audio Selector 1",
					AudioSelector.builder().defaultSelection(AudioDefaultSelection.DEFAULT).offset(0).build());

			// Create VideoSelector
			VideoSelector videoSelector = VideoSelector.builder().colorSpace(ColorSpace.FOLLOW)
					.rotate(InputRotate.DEGREE_0).build();

			// Create Output Groups
			Output highBitrateHlsOutput = createOutput("hls_medium", nameModifier + "_high", "_$dt$", 5000000, 7, 1920,
					1080, 1280);

			Output lowBitrateHlsOutput = createOutput("hls_medium", nameModifier + "_low", "_$dt$", 500000, 7, 1920,
					1080, 1280);
			OutputGroupSettings outputGroupSettings = OutputGroupSettings.builder()
					.type(OutputGroupType.HLS_GROUP_SETTINGS)
					.hlsGroupSettings(createHlsGroupSettings(completeOutputS3VideoPathWithoutType)).build();

			OutputGroup appleHlsOutputGroup = OutputGroup.builder().name("Apple HLS").customName("Convert video to HLS")
					.outputGroupSettings(outputGroupSettings).outputs(highBitrateHlsOutput, lowBitrateHlsOutput)
					.build();

			// Thumbs group
			FileGroupSettings thumbsFileGroupSettings = FileGroupSettings.builder().destination(s3UriThumbsPath)
					.build();
			OutputGroupSettings thumbsOutputGroupSettings = OutputGroupSettings.builder()
					.type(OutputGroupType.FILE_GROUP_SETTINGS).fileGroupSettings(thumbsFileGroupSettings).build();
			Output thumbsOutput = createThumbsOutput("Create thumbs", nameModifier, "_$dt$", 1200000, 7, 1920, 1080,
					1280);
			OutputGroup thumbsOutputGroup = OutputGroup.builder().name("Thumbs Group").customName("Create Thumbs")
					.outputGroupSettings(thumbsOutputGroupSettings).outputs(thumbsOutput).build();

			OutputGroup[] outputGroups = new OutputGroup[2];
			outputGroups[0] = appleHlsOutputGroup;
			outputGroups[1] = thumbsOutputGroup;

			// Create JobSettings
			JobSettings jobSettings = JobSettings.builder()
					.inputs(Input.builder().audioSelectors(audioSelectors).videoSelector(videoSelector)
							.filterEnable(InputFilterEnable.AUTO).filterStrength(0)
							.deblockFilter(InputDeblockFilter.DISABLED).denoiseFilter(InputDenoiseFilter.DISABLED)
							.psiControl(InputPsiControl.USE_PSI).timecodeSource(InputTimecodeSource.EMBEDDED)
							.fileInput(fileInput).build())
					.outputGroups(outputGroups).build();

			// Create Job
			CreateJobRequest createJobRequest = CreateJobRequest.builder().role(mediaConvertRole).settings(jobSettings)
					.build();

			CreateJobResponse createJobResponse = emc.createJob(createJobRequest);

			return createJobResponse.job().id();

		} catch (MediaConvertException e) {
			log.error("Error converting video to hls {}", e);
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CONVERTING_VIDEO_TO_HLS_FAILED");
		}
	}

	private HlsGroupSettings createHlsGroupSettings(String completeOutputS3VideoPathWithoutType) {

		return HlsGroupSettings.builder().directoryStructure(HlsDirectoryStructure.SINGLE_DIRECTORY)
				.manifestDurationFormat(HlsManifestDurationFormat.INTEGER)
				.streamInfResolution(HlsStreamInfResolution.INCLUDE).clientCache(HlsClientCache.ENABLED)
				.captionLanguageSetting(HlsCaptionLanguageSetting.OMIT).manifestCompression(HlsManifestCompression.NONE)
				.codecSpecification(HlsCodecSpecification.RFC_4281)
				.outputSelection(HlsOutputSelection.MANIFESTS_AND_SEGMENTS).programDateTime(HlsProgramDateTime.EXCLUDE)
				.programDateTimePeriod(600).timedMetadataId3Frame(HlsTimedMetadataId3Frame.PRIV)
				.timedMetadataId3Period(10).destination(completeOutputS3VideoPathWithoutType)
				.segmentControl(HlsSegmentControl.SEGMENTED_FILES).minFinalSegmentLength((double) 0).segmentLength(2)
				.minSegmentLength(0).build();

	}

	private Output createOutput(String customName, String nameModifier, String segmentModifier, int qvbrMaxBitrate,
			int qvbrQualityLevel, int originWidth, int originHeight, int targetWidth) {

		int targetHeight = Math.round(originHeight * targetWidth / originWidth)
				- (Math.round(originHeight * targetWidth / originWidth) % 4);
		Output output = null;
		try {
			output = Output.builder().nameModifier(nameModifier).outputSettings(OutputSettings.builder()
					.hlsSettings(HlsSettings.builder().segmentModifier(segmentModifier).audioGroupId("program_audio")
							.iFrameOnlyManifest(HlsIFrameOnlyManifest.EXCLUDE).build())
					.build())
					.containerSettings(ContainerSettings.builder().container(ContainerType.M3_U8)
							.m3u8Settings(M3u8Settings.builder().audioFramesPerPes(4)
									.pcrControl(M3u8PcrControl.PCR_EVERY_PES_PACKET).pmtPid(480).privateMetadataPid(503)
									.programNumber(1).patInterval(0).pmtInterval(0).scte35Source(M3u8Scte35Source.NONE)
									.scte35Pid(500).nielsenId3(M3u8NielsenId3.NONE).timedMetadata(TimedMetadata.NONE)
									.timedMetadataPid(502).videoPid(481)
									.audioPids(482, 483, 484, 485, 486, 487, 488, 489, 490, 491, 492).build())
							.build())
					.videoDescription(
							VideoDescription.builder().scalingBehavior(ScalingBehavior.DEFAULT).sharpness(50)
									.antiAlias(AntiAlias.ENABLED).timecodeInsertion(VideoTimecodeInsertion.DISABLED)
									.colorMetadata(ColorMetadata.INSERT).respondToAfd(RespondToAfd.NONE)
									.afdSignaling(AfdSignaling.NONE).dropFrameTimecode(DropFrameTimecode.ENABLED)
									.codecSettings(VideoCodecSettings.builder().codec(VideoCodec.H_264)
											.h264Settings(H264Settings.builder()
													.rateControlMode(H264RateControlMode.QVBR)
													.parControl(H264ParControl.INITIALIZE_FROM_SOURCE)
													.qualityTuningLevel(H264QualityTuningLevel.SINGLE_PASS)
													.qvbrSettings(H264QvbrSettings.builder()
															.qvbrQualityLevel(qvbrQualityLevel).build())
													.codecLevel(H264CodecLevel.AUTO)
													.codecProfile((targetHeight > 720 && targetWidth > 1280)
															? H264CodecProfile.HIGH
															: H264CodecProfile.MAIN)
													.maxBitrate(qvbrMaxBitrate)
													.framerateControl(H264FramerateControl.INITIALIZE_FROM_SOURCE)
													.gopSize(2.0).gopSizeUnits(H264GopSizeUnits.SECONDS)
													.numberBFramesBetweenReferenceFrames(2).gopClosedCadence(1)
													.gopBReference(H264GopBReference.DISABLED)
													.slowPal(H264SlowPal.DISABLED).syntax(H264Syntax.DEFAULT)
													.numberReferenceFrames(3).dynamicSubGop(H264DynamicSubGop.STATIC)
													.fieldEncoding(H264FieldEncoding.PAFF)
													.sceneChangeDetect(H264SceneChangeDetect.ENABLED).minIInterval(0)
													.telecine(H264Telecine.NONE)
													.framerateConversionAlgorithm(
															H264FramerateConversionAlgorithm.DUPLICATE_DROP)
													.entropyEncoding(H264EntropyEncoding.CABAC).slices(1)
													.unregisteredSeiTimecode(H264UnregisteredSeiTimecode.DISABLED)
													.repeatPps(H264RepeatPps.DISABLED)
													.adaptiveQuantization(H264AdaptiveQuantization.HIGH)
													.spatialAdaptiveQuantization(
															H264SpatialAdaptiveQuantization.ENABLED)
													.temporalAdaptiveQuantization(
															H264TemporalAdaptiveQuantization.ENABLED)
													.flickerAdaptiveQuantization(
															H264FlickerAdaptiveQuantization.DISABLED)
													.softness(0).interlaceMode(H264InterlaceMode.PROGRESSIVE).build())
											.build())
									.build())
					.audioDescriptions(AudioDescription.builder().audioTypeControl(AudioTypeControl.FOLLOW_INPUT)
							.languageCodeControl(AudioLanguageCodeControl.FOLLOW_INPUT)
							.codecSettings(AudioCodecSettings.builder().codec(AudioCodec.AAC).aacSettings(AacSettings
									.builder().codecProfile(AacCodecProfile.LC).rateControlMode(AacRateControlMode.CBR)
									.codingMode(AacCodingMode.CODING_MODE_2_0).sampleRate(44100).bitrate(96000)
									.rawFormat(AacRawFormat.NONE).specification(AacSpecification.MPEG4)
									.audioDescriptionBroadcasterMix(AacAudioDescriptionBroadcasterMix.NORMAL).build())
									.build())
							.build())
					.build();
		} catch (MediaConvertException e) {
			log.error("Error finidng end point to convert video {}", e.getCause());
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ADD_NEW_VIDEO_FAILED");
		}
		return output;
	}

	private Output createThumbsOutput(String customName, String nameModifier, String segmentModifier,
			int qvbrMaxBitrate, int qvbrQualityLevel, int originWidth, int originHeight, int targetWidth) {

		Output thumbsOutput = Output.builder().extension("jpg")
				.containerSettings(ContainerSettings.builder().container(ContainerType.RAW).build())
				.videoDescription(VideoDescription.builder().scalingBehavior(ScalingBehavior.DEFAULT).sharpness(50)
						.antiAlias(AntiAlias.ENABLED).timecodeInsertion(VideoTimecodeInsertion.DISABLED)
						.colorMetadata(ColorMetadata.INSERT).dropFrameTimecode(DropFrameTimecode.ENABLED)
						.codecSettings(VideoCodecSettings.builder().codec(VideoCodec.FRAME_CAPTURE)
								.frameCaptureSettings(FrameCaptureSettings.builder().framerateNumerator(1)
										.framerateDenominator(1).maxCaptures(10000000).quality(80).build())
								.build())
						.build())
				.build();

		return thumbsOutput;
	}

	private void setVideoDimensions(String jobId, Story story, MediaConvertClient mediaConvertClient)
			throws InterruptedException {

		GetJobRequest jobRequest = GetJobRequest.builder().id(jobId).build();
		Job job = mediaConvertClient.getJob(jobRequest).job();

		List<OutputGroupDetail> outputGroupDetailList = job.outputGroupDetails();
		log.info("Job output details size is {}", outputGroupDetailList.size());

		int tryNumber = 1;
		while (outputGroupDetailList.size() == 0 && tryNumber < SET_DIMENSIONS_MAX_TRIES) {
			log.info("Try number {}", tryNumber);
			log.info("Wait {} second(s)", SET_DIMENSIONS_WAIT_TIME_SECONDS);
			Thread.sleep(SET_DIMENSIONS_WAIT_TIME_SECONDS * 1000);

			jobRequest = GetJobRequest.builder().id(jobId).build();
			job = mediaConvertClient.getJob(jobRequest).job();
			outputGroupDetailList = job.outputGroupDetails();
			log.info("Job output details size is {}", outputGroupDetailList.size());
			tryNumber++;
		}
		if (outputGroupDetailList.size() > 0) {
			OutputGroupDetail outputGroupDetail = outputGroupDetailList.get(0);
			List<OutputDetail> outputDetailsList = outputGroupDetail.outputDetails();
			OutputDetail outputDetail = outputDetailsList.get(0);
			int videoWidth = outputDetail.videoDetails().widthInPx();
			int videoHeight = outputDetail.videoDetails().heightInPx();

			log.info("storyVideoWidth is {}", videoWidth);
			log.info("storyVideoHeight is {}", videoHeight);

//			story.getMediaDetails().setWidth(videoWidth);
//			story.getMediaDetails().setHeight(videoHeight);
		}
	}

	private void verifyJob(MediaConvertClient emc, String jobId, int tries, int jobWaitTime)
			throws InterruptedException {
		log.debug("Verify job status for jobId {} try number {}", jobId, tries);

		TimeUnit.SECONDS.sleep(jobWaitTime);

		GetJobRequest jobRequest = GetJobRequest.builder().id(jobId).build();

		GetJobResponse response = emc.getJob(jobRequest);

		JobStatus jobStatus = response.job().status();

		switch (jobStatus) {
		case COMPLETE:
			return;

		case ERROR:
		case CANCELED:
		case UNKNOWN_TO_SDK_VERSION:
			log.error("Error getting job {} complete", jobId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ADD_NEW_VIDEO_FAILED");

		case PROGRESSING:
		case SUBMITTED:
			if (tries <= MAX_TRIES) {
				verifyJob(emc, jobId, tries + 1, jobWaitTime);
			}
			break;
		default:
			log.error("Error getting job {} complete, invalid status", jobId);
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ADD_NEW_VIDEO_FAILED");

		}

	}
}