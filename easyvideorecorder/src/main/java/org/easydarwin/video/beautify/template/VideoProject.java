package org.easydarwin.video.beautify.template;

import java.util.List;

public class VideoProject {

	private VideoTemplate videoTemplate;

	public VideoProject(VideoTemplate videoTemplate) {
		if (videoTemplate == null) {
			throw new IllegalStateException("videoTemplate can not be null!");
		}
		this.videoTemplate = videoTemplate;
	}

	public VideoTemplate getVideoTemplate() {
		return videoTemplate;
	}

	public void setVideoTemplate(VideoTemplate videoTemplate) {
		this.videoTemplate = videoTemplate;
	}

	/**
	 * 把素材绑定到模板对应节点（暂时只实现视频轨素材绑定）
	 * 
	 * @param mediaClip
	 * @return
	 */
	public void addMedia(List<MediaClip> mediaClips) {
		if (getVideoTemplate() == null || getVideoTemplate().getRenderTree() == null) {
			throw new IllegalStateException("videoTemplate and RenderTree should not be null!");
		}
		long totalFrame = 0;
		TimeLineNode renderTree = getVideoTemplate().getRenderTree();
		List<TimeLineNode> trackList = renderTree.getChildNodeList();
		for (TimeLineNode trackNode : trackList) {
			if (trackNode.getNodeType() == NodeType.VIDEO_TRACK) {
				List<TimeLineNode> videoNodes = trackNode.getChildNodeList();
				int mediaIndex = 0;
				TimeLineNode videoNode = null;
				for (TimeLineNode timeLineNode : videoNodes) {
					videoNode = timeLineNode;
					if (videoNode.getNodeData() != null) {
						// 模板自带素材节点，如“片头”
						continue;
					}
					MediaClip clip = mediaClips.get(mediaIndex++);
					videoNode.setNodeData(clip);
					totalFrame += clip.getDuration();
					if (clip.getDuration() < videoNode.getDuration()) {
						videoNode.setDuration(clip.getDuration());
					}
					TimeLineNode filterNode = videoNode.getChildNodeList().get(0);
					FilterGroup filterGroup = (FilterGroup) filterNode.getNodeData();
					if (filterGroup.getEffect() == null) {
						Filter filter = FilterUtils.buildBlankFilter();
						filter.setOffset(videoNode.getOffset());
						filter.setDuration(videoNode.getDuration());
						filterGroup.addFilter(filter);
					}

					if (mediaIndex == mediaClips.size()) {
						break;
					}

				}
				if (mediaIndex < mediaClips.size()) {
					for (int i = 0; i < mediaClips.size() - mediaIndex; i++) {

					}
				}

			}
		}
		videoTemplate.setTotalFrame(totalFrame);
	}

}
