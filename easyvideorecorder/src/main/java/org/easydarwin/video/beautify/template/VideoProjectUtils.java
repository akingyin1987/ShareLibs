package org.easydarwin.video.beautify.template;

import java.io.File;
import java.util.List;

import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.beautify.model.ProcessTask;
import org.easydarwin.video.beautify.task.VideoProcessor;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.SystemClock;
import android.util.Log;

public class VideoProjectUtils {
	private final static String LOG_TAG = "ExcecuteProject";
	private VideoProcessor videoProcessor = null;

	public void excute(VideoProcessor newVideoProcessor, Context ctx) {
		videoProcessor = newVideoProcessor;
		ProcessTask processTask = videoProcessor.getProcessTask();

		VideoProject project = buildVideoProject(processTask);

		excuteProject(project, ctx);
	}

	private static VideoProject buildVideoProject(ProcessTask processTask) {
		VideoProject project = null;

		VideoTemplate template = null;
		if (processTask.getThemeId() != null && !processTask.getThemeId().equals("0")) {
			template = VideoTemplateUtils.buildFromXML(processTask.getThemeId());
		} else {
			// create a blank template
			template = VideoTemplateUtils.createBlankTemplate();
		}

		if (processTask.getFilterId() != null && !processTask.getFilterId().equals("0")) {
			Filter filter = FilterUtils.buildFromXML(processTask.getFilterId());
			filter.setOffset(0);
			filter.setDuration(Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE);
			template.addFilter(filter, 0);
		}

		if (processTask.getTitlles() != null) {
			if (processTask.getDecorateFilter() == null) {
				Filter blank = FilterUtils.buildBlankFilter();
				blank.setOffset(0);
				blank.setDuration(Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE);
				template.addFilter(blank);
			}
			// template.addTittle(processTask.getTitlles());
			for (int i = 0; i < processTask.getTitlles().length; i++) {
				template.addFilter(processTask.getTitlles()[i].getFilter());
			}
		}

		if (processTask.getDecorateFilter() != null) {
			if (processTask.getTitlles() == null) {
				Filter blank = FilterUtils.buildBlankFilter();
				blank.setOffset(0);
				blank.setDuration(Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE);
				template.addFilter(blank);
			}
			template.addFilter(processTask.getDecorateFilter());
		}
		if (processTask.getMusic() != null) {
			template.addMusic(processTask.getMusic());
		}

		if (processTask.getAudioClips() != null) {
			template.addAudio(processTask.getAudioClips());
		}

		project = new VideoProject(template);
		List<MediaClip> mediaClips = MediaMgr.buildMediaClip(AssetType.VIDEO, processTask.getUri());
		project.addMedia(mediaClips);

		if (VideoTemplateUtils.ADD_WATERMARK) {
			VideoTemplateUtils.addWatermarkNode(template);
		}

		return project;
	}

	protected Uri excuteProject(VideoProject videoProject, Context ctx) {

		// 处理进度
		int progress = 0;

		VideoTemplate videoTemplate = videoProject.getVideoTemplate();

		// 输出文件：暂定为输入文件同目录，文件名加上模板Id
		Uri outVideo = null;
		VideoAsset outVideoAsset = null;
		if (!videoProcessor.isPreview()) {
			String outPath = videoProcessor.getOutPath();
			outVideo = Uri.fromFile(new File(outPath, videoTemplate.getId() + System.currentTimeMillis() + ".mp4"));
			outVideoAsset = new VideoAsset();
			outVideoAsset.setUri(outVideo);
		}

		long totalFrame = videoTemplate.getTotalFrame();
		TimeLineNode renderTree = videoTemplate.getRenderTree();
		List<TimeLineNode> trackList = renderTree.getChildNodeList();

		// 帧图像
		Bitmap curFrame = null;
		// 滤镜特效
		FilterGroup filter = null;
		VideoClip videoClip = null;

		// 音频轨处理
		// AudioClip audioClip = null;
		AudioGroup audioClips = getAudioGroup(videoProject);

		try {
			long time = System.currentTimeMillis();
			Log.i(LOG_TAG, "start filter:" + time);
			for (int frameIndex = 0; frameIndex < totalFrame; frameIndex++) {
				if (videoProcessor.isSynthesis()) {
					videoProcessor.setProcessedUri(outVideo);
					videoProcessor.setProgress(100);
					videoProcessor.setDone(true);
					return outVideo;
				}

				curFrame = null;
				filter = null;
				long framestart = System.currentTimeMillis();
				// 1.遍历时间线的每个轨，获取帧图像、特效、音轨、字幕
				for (TimeLineNode track : trackList) {
					switch (track.getNodeType()) {
					/** 视频轨 */
						case VIDEO_TRACK:
							List<TimeLineNode> videoNodes = track.getChildNodeList();
							for (TimeLineNode videoNode : videoNodes) {
								// 取入点到出点包含当前帧序号的节点
								if (frameIndex >= videoNode.getOffset() && frameIndex < videoNode.getOutPoint()) {

									// 取绑定到节点的素材 video or imageSeq?
									switch (videoNode.getNodeType()) {
										case VIDEO_NODE:
											if (videoClip != null && videoClip.getAsset() != ((VideoClip) videoNode.getNodeData()).getAsset()) {
												videoClip.getAsset().closeDecode();
											}
											videoClip = (VideoClip) videoNode.getNodeData();

											// 取视频节点指定的滤镜
											List<TimeLineNode> filterNodes = videoNode.getChildNodeList();
											if (filterNodes != null) {
												// 只有一个滤镜节点
												TimeLineNode filterNode = filterNodes.get(0);
												if (filter != null && filter != filterNode.getNodeData()) {
													filter.close();
												}
												filter = (FilterGroup) filterNode.getNodeData();
											}

											break;

										case IMAGE_NODE:

											break;

										default:
											break;
									} // end switch

								} // end if
								/*
								 * else if (frameIndex >= videoNode.getOutPoint() -
								 * 1) { // 关闭视频解码器 if (videoClip != null) {
								 * videoClip.getAsset().closeDecode(); } if(filter
								 * != null){ filter.close(); }
								 * 
								 * }
								 */

							} // end vedioNodes

							break;

						case AUDIO_TRACK:
							/*
							 * List<TimeLineNode> audioNodes =
							 * track.getChildNodeList(); for (Iterator<TimeLineNode>
							 * iterator2 = audioNodes.iterator();
							 * iterator2.hasNext();) { TimeLineNode audioNode =
							 * (TimeLineNode) iterator2.next(); // 取入点到出点包含当前帧序号的节点
							 * if (frameIndex >= audioNode.getOffset() && frameIndex
							 * <= audioNode.getOutPoint()) { audioClip =
							 * (AudioClip)audioNode.getNodeData(); } else if
							 * (frameIndex >= audioNode.getOutPoint() - 1) { if
							 * (audioClip != null) { audioClip.close(); } } }
							 */
							break;

						case TRANSITION_TRACK:

							break;

						default:
							break;
					} // end switch

				} // end iterator

				if (frameIndex == 0) {
					// 调用解码接口开始解码，获取帧图像
					videoClip.startDecode();

					if (!videoProcessor.isPreview()) {
						// ////////////////////////////////////////////
						outVideoAsset.setWidth(videoClip.getAsset().getWidth());
						outVideoAsset.setHeight(videoClip.getAsset().getHeight());
						// ////////////////////////////////////////////
						outVideoAsset.startEncode();

						videoClip.getAsset().setMediaTarget(outVideoAsset.getMediaTarget());
						// 消除原音
						videoClip.getAsset().setUseSrcAudio(!videoProcessor.getProcessTask().isMute());
					} else {// 预览
						// 播放原音
						AudioEffect.playSrcAudio(videoClip.getAsset().getUri(), ctx);
						AudioEffect.setSrcPlayerMute(videoProcessor.getProcessTask().isMute());
					}
				}
				//Log.e("frameIndex=====>", frameIndex + "");
				// 获取帧图像
				curFrame = videoClip.getNextFrame();
				if (curFrame == null) {
					break;
					// 异常处理
				}

				// 2.调用特效接口处理帧图像
				if (filter != null) {
					filter.setFrameIndex(frameIndex);
					curFrame = filter.applyEffect(curFrame, videoProcessor.isPreview());
				}
				// 音频效果
				audioClips.setFrameIndex(frameIndex);
				if (videoProcessor.isPreview()) {
					audioClips.applyEffect(ctx);
				} else {
					videoClip.getAsset().setMusic(audioClips);
				}

				// ////////////////////////////////////////////////////
				// 计算处理进度
				if (videoProcessor != null) {
					progress = (int) ((float) frameIndex / totalFrame * 100) + 1;
					videoProcessor.setProgress(progress + 1);
					if (videoProcessor.getBit() == null) {
						videoProcessor.setBit(curFrame);
					}
				}
				if (videoProcessor.isPreview()) {
					long end = System.currentTimeMillis() - framestart;
					if (end < videoProcessor.getFrameRate()) {
						SystemClock.sleep(videoProcessor.getFrameRate() - end);
					}
				} else {
					// 3.调用编码接口合成视频
					outVideoAsset.appendFrame(curFrame);
				}
			} // end for

			if (!videoProcessor.isPreview()) {
				outVideoAsset.colseEncode();
			}
			Log.i(LOG_TAG, "end filter:" + (System.currentTimeMillis() - time));
		} catch (Throwable e) {
			Log.e(LOG_TAG, "" + videoTemplate.getId() + videoTemplate.getName(), e);
			e.printStackTrace();
		} finally {
			if (videoClip != null) {
				videoClip.getAsset().closeDecode();
			}
			if (filter != null) {
				filter.close();
			}
			if (audioClips != null) {
				audioClips.close();
			}
			if (curFrame != null) {
				// curFrame.recycle();
			}
			// System.gc();
			AudioEffect.stopSrcPlayer();
		}

		videoProcessor.setProcessedUri(outVideo);
		// ///////////////////////////////////////////////
		// 结束标记
		if (videoProcessor != null) {
			videoProcessor.setProcessedUri(outVideo);
			videoProcessor.setProgress(100);
			videoProcessor.setDone(true);
		}
		// ///////////////////////////////////////////////

		return outVideo;
	}

	private AudioGroup getAudioGroup(VideoProject videoProject) {
		//
		AudioGroup audioClips = new AudioGroup();
		TimeLineNode renderTree = videoProject.getVideoTemplate().getRenderTree();
		List<TimeLineNode> trackList = renderTree.getChildNodeList();
		for (TimeLineNode track : trackList) {
			if (track.getNodeType() == NodeType.AUDIO_TRACK) {
				List<TimeLineNode> audioNodes = track.getChildNodeList();
				for (TimeLineNode audioNode : audioNodes) {
					audioClips.addAudio((AudioClip) audioNode.getNodeData());
				}
				break;
			}
		}

		return audioClips;
	}

	/**
	 * 一、简单应用，直接指定滤镜处理素材
	 * 
	 * @param video
	 *           素材
	 * @param filterId
	 *           滤镜类型
	 * @return 处理后的视频文件
	 */
	public Uri applyFilter(String filterId, Context ctx, Uri... videos) {
		//
		List<MediaClip> mediaClips = MediaMgr.buildMediaClip(AssetType.VIDEO, videos);

		//
		VideoTemplate simpleTemplate = VideoTemplateUtils.createSimpleTemplate(filterId);

		VideoProject project = new VideoProject(simpleTemplate);
		project.addMedia(mediaClips);

		// processVideoUri = videos[0];

		return excuteProject(project, ctx);
	}

	/**
	 * 二、选择模板，处理素材
	 * 
	 * @param video
	 *           素材
	 * @param videoTemplateId
	 *           视频处理模板
	 * @return 处理后的视频文件
	 */
	public Uri applyVideoTemplate_xxx(String videoTemplateId, Context ctx, Uri... videos) {

		List<MediaClip> mediaClips = MediaMgr.buildMediaClip(AssetType.VIDEO, videos);

		//
		VideoTemplate videoTemplate = VideoTemplateUtils.buildFromXML(videoTemplateId);

		VideoProject project = new VideoProject(videoTemplate);
		project.addMedia(mediaClips);

		// processVideoUri = videos[0];

		return excuteProject(project, ctx);

	}

	// 三、自定义模板（对一系列自定义操作的描述），或自定义模板元素（替换模板元素）
	//

}
