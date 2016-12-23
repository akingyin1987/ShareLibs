package org.easydarwin.video.beautify.template;

// 渲染树节点类型
public enum NodeType {
	VIDEO_TRACK, //视频轨
	TRANSITION_TRACK, //转场特效轨
	FXEFFECT_TRACK, //Fx特效轨
	AUDIO_TRACK, //音频轨
	TITTLE_TRACK, //字幕轨

	VIDEO_NODE, //视频素材
	IMAGE_NODE, //图像素材
	FILER_NODE, //滤镜
	TRANSITION_NODE, //转场特效
	FXEFFECT_NODE, //Fx特效
	TITTLE_NODE //字幕
}
