package com.zlcdgroup.libs.photovideo.vo;

import com.zlcdgroup.libs.photovideo.BaseImgTextItem;

import java.io.Serializable;
import java.util.List;

public class ImageTextsVo  implements  Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private   List<BaseImgTextItem>    imgs ;

	public List<BaseImgTextItem> getImgs() {
		return imgs;
	}

	public void setImgs(List<BaseImgTextItem> imgs) {
		this.imgs = imgs;
	}
	
	
	

}
