package org.easydarwin.video.beautify.template;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.easydarwin.video.beautify.conf.Conf;

/**
 * 
 * 视频处理模板
 * 
 */
public class VideoTemplate {

	private String id;

	private String name;

	// 总时长（帧）
	private long totalFrame = Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE;

	// 帧率
	private int frameRate = Conf.VIDEO_FRAMERATE;

	// frameSize
	private int width;

	private int height;

	// 渲染树
	private TimeLineNode renderTree;

	public VideoTemplate() {
	}

	public VideoTemplate(String id) {
		if (id == null) {
			throw new IllegalStateException("VideoTemplate: id can not be null!");
		}
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TimeLineNode getRenderTree() {
		return renderTree;
	}

	public void setRenderTree(TimeLineNode renderTree) {
		this.renderTree = renderTree;
	}

	public long getTotalFrame() {
		return totalFrame;
	}

	public void setTotalFrame(long totalFrame) {
		this.totalFrame = totalFrame;
	}

	public int getFrameRate() {
		return frameRate;
	}

	public void setFrameRate(int frameRate) {
		this.frameRate = frameRate;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void addFilter(Filter filter) {
		addFilter(filter, -1);
	}

	public void addFilter(Filter filter, int index) {

		List<TimeLineNode> trackList = getRenderTree().getChildNodeList();

		TimeLineNode videoTrack = null;
		for (TimeLineNode trackNode : trackList) {
			if (trackNode.getNodeType() == NodeType.VIDEO_TRACK) {
				videoTrack = trackNode;
				break;
			}
		}

		List<TimeLineNode> videoNodes = videoTrack.getChildNodeList();
		TimeLineNode videoNode = null;
		for (Iterator<TimeLineNode> iterator2 = videoNodes.iterator(); iterator2.hasNext();) {
			videoNode = iterator2.next();
			if (videoNode.getNodeData() != null) {
				continue;
			}
			// /////////////////////////////////////////////////

			TimeLineNode filterNode = videoNode.getChildNodeList().get(0);
			FilterGroup filterGroup = (FilterGroup) filterNode.getNodeData();
			//			
			if (index >= 0) {
				int size = filterGroup.getFilterSize();
				if (size == 0) {
					size += 1;
				}
				size += filter.getFilterSize();
				if (size % 2 == 0) {
					Filter blank = FilterUtils.buildBlankFilter();
					blank.setOffset(filter.getOffset());
					blank.setDuration(filter.getDuration());
					filterGroup.addFilter(blank, index++);
				}
				filterGroup.addFilter(filter, index);
			} else {
				//
				if (filterGroup.getFilters().size() > 0) {
					Filter lastFilter = filterGroup.getFilters().get(filterGroup.getFilters().size() - 1);
					if (lastFilter.getFilterType().startsWith("BLEND")) {
						Filter blank = FilterUtils.buildBlankFilter();
						blank.setOffset(filter.getOffset());
						blank.setDuration(filter.getDuration());
						filterGroup.addFilter(blank);
					}
				} else {
					//  场景：无主题&无滤镜&添加字幕
					//					if (filter.getOffset() > 0) {
					//						Filter blank = FilterUtils.buildBlankFilter();
					//						blank.setOffset(0);
					//						blank.setDuration(filter.getOffset());
					//						filterGroup.addFilter(blank);
					//					}
					//					if (filter.getDuration() < videoNode.getDuration()) {
					//						Filter blank = FilterUtils.buildBlankFilter();
					//						blank.setOffset(filter.getOffset() + filter.getDuration());
					//						blank.setDuration(videoNode.getDuration() - filter.getDuration());
					//						filterGroup.addFilter(blank);
					//					}
					//					if (filter.getOffset() == 0 && filter.getDuration() == videoNode.getDuration()) {
					//						Filter blank = FilterUtils.buildBlankFilter();
					//						blank.setOffset(filter.getOffset());
					//						blank.setDuration(filter.getDuration());
					//						filterGroup.addFilter(blank);
					//					}
				}
				//
				filterGroup.addFilter(filter);
			}
		}

	}

	/**
	 * 增加字幕素效果
	 * 
	 * @return
	 */
	public void addTittle(Tittle[] tittles) {

		List<TimeLineNode> trackList = getRenderTree().getChildNodeList();

		TimeLineNode tittleTrack = null;
		for (TimeLineNode trackNode : trackList) {
			if (trackNode.getNodeType() == NodeType.TITTLE_TRACK) {
				tittleTrack = trackNode;
				break;
			}
		}
		if (tittleTrack == null) {
			tittleTrack = new TimeLineNode();
			tittleTrack.setName("Tittle");
			tittleTrack.setNodeType(NodeType.TITTLE_TRACK);
			tittleTrack.setParentNode(renderTree);
			trackList.add(tittleTrack);
		}
		if (tittleTrack.getChildNodeList() == null) {
			tittleTrack.setChildNodeList(new ArrayList<TimeLineNode>());
		}

		for (Tittle tittle : tittles) {
			TimeLineNode tittleNode = new TimeLineNode();
			tittleNode.setNodeType(NodeType.TITTLE_NODE);
			tittleNode.setOffset(tittle.getOffset());
			tittleNode.setDuration(tittle.getDuration());
			tittleNode.setNodeData(tittle);

			tittleTrack.getChildNodeList().add(tittleNode);
		}

	}

	/**
	 * 增加音频效果
	 * 
	 * @return
	 */
	public void addAudio(AudioClip[] audioClips) {

		List<TimeLineNode> trackList = getRenderTree().getChildNodeList();

		TimeLineNode audioTrack = null;
		for (TimeLineNode trackNode : trackList) {
			if (trackNode.getNodeType() == NodeType.AUDIO_TRACK) {
				audioTrack = trackNode;
				break;
			}
		}
		if (audioTrack == null) {
			audioTrack = new TimeLineNode();
			audioTrack.setName("Audio");
			audioTrack.setNodeType(NodeType.AUDIO_TRACK);
			audioTrack.setParentNode(renderTree);
			trackList.add(audioTrack);
		}
		if (audioTrack.getChildNodeList() == null) {
			audioTrack.setChildNodeList(new ArrayList<TimeLineNode>());
		}

		for (AudioClip audioClip : audioClips) {
			TimeLineNode audioNode = new TimeLineNode();
			audioNode.setNodeType(NodeType.AUDIO_TRACK);
			audioNode.setOffset(audioClip.getOffset());
			audioNode.setDuration(audioClip.getDuration());
			audioNode.setNodeData(audioClip);
			audioClip.setEffect(null);

			audioTrack.getChildNodeList().add(audioNode);
		}

	}

	/**
	 * 用本地音乐替换模板音乐
	 * 
	 * @return
	 */
	public void addMusic(AudioClip music) {

		List<TimeLineNode> trackList = getRenderTree().getChildNodeList();

		TimeLineNode audioTrack = null;
		for (TimeLineNode trackNode : trackList) {
			if (trackNode.getNodeType() == NodeType.AUDIO_TRACK) {
				audioTrack = trackNode;
				break;
			}
		}
		if (audioTrack == null) {
			audioTrack = new TimeLineNode();
			audioTrack.setName("Audio");
			audioTrack.setNodeType(NodeType.AUDIO_TRACK);
			audioTrack.setParentNode(renderTree);
			trackList.add(audioTrack);
		}
		if (audioTrack.getChildNodeList() == null) {
			audioTrack.setChildNodeList(new ArrayList<TimeLineNode>());
		}

		if (audioTrack.getChildNodeList().size() > 0) {
			audioTrack.getChildNodeList().remove(0);
		}

		TimeLineNode audioNode = new TimeLineNode();
		audioNode.setNodeType(NodeType.AUDIO_TRACK);
		audioNode.setOffset(music.getOffset());
		audioNode.setDuration(music.getDuration());
		audioNode.setNodeData(music);
		music.setEffect(null);

		audioTrack.getChildNodeList().add(audioNode);
	}

}
