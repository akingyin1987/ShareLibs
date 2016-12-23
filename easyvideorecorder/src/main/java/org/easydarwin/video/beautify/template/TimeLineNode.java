package org.easydarwin.video.beautify.template;

import java.util.List;

import org.easydarwin.video.beautify.conf.Conf;

/**
 * 
 * 渲染树节点
 *
 */
public class TimeLineNode {

	private String id;

	private String name;

	private TimeLineNode parentNode;

	private List<TimeLineNode> childNodeList;

	private NodeType nodeType;

	private MediaClip nodeData;

	// 入点（精确帧）
	private long offset = 0;

	// -->出点
	private long duration = Conf.V_RE_MAX / 1000 * Conf.VIDEO_FRAMERATE;

	// 是否对齐循环
	private boolean recycle = true;

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

	public TimeLineNode getParentNode() {
		return parentNode;
	}

	public void setParentNode(TimeLineNode parentNode) {
		this.parentNode = parentNode;
	}

	public List<TimeLineNode> getChildNodeList() {
		return childNodeList;
	}

	public void setChildNodeList(List<TimeLineNode> childNodeList) {
		this.childNodeList = childNodeList;
	}

	public NodeType getNodeType() {
		return nodeType;
	}

	public void setNodeType(NodeType nodeType) {
		this.nodeType = nodeType;
	}

	public MediaClip getNodeData() {
		return nodeData;
	}

	public void setNodeData(MediaClip nodeData) {
		this.nodeData = nodeData;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public boolean isRecycle() {
		return recycle;
	}

	public void setRecycle(boolean recycle) {
		this.recycle = recycle;
	}

	public long getOutPoint() {
		return offset + duration;
	}

}
