package com.zlcdgroup.libs.photovideo;
import java.io.File;
import java.io.Serializable;
import android.text.TextUtils;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.zlcdgroup.libs.photovideo.vo.TempBaseVo;
import com.zlcdgroup.libs.utils.FileUtil;


public abstract class BaseImgTextItem extends Model implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 本地图片绝对路径

	@Column(name = "localPath")
	public String localPath;

	// 服务器路径相对路径

	@Column(name = "webPath")
	public String webPath;

	// 文本描述
	@Column(name = "textdesc")
	public String textdesc;
	

	@Column(name="originalLocalPath")
	public String  originalLocalPath;//原始图片
	

	@Column(name = "copyName")
	public String copyName;// 复制图片名称

	@Column(name = "style")
	public int style;// 1=文字 2=图片 3=视频

	// 顺序
	@Column(name = "sort")
	public int sort;


	public boolean isImageExist() {
		if (TextUtils.isEmpty(localPath)) {
			return false;
		}
		try {
			File file = new File(localPath);
			return file.exists();

		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;

	
	}


	public boolean delectImage() {
		if (TextUtils.isEmpty(localPath)) {
			return false;
		}
		try {
			File file = new File(localPath);
			if (file.exists()) {
				return file.delete();
			}
		} catch (Exception e) {

		}
		return false;

	}
	

	public   boolean   isEmpty(){
		if(TextUtils.isEmpty(textdesc) && TextUtils.isEmpty(localPath)){
			return  true;
		}
		return false;
	}
	
	// 是否被选中

		private boolean ischecked;

		public boolean isIschecked() {
			return ischecked;
		}

		public void setIschecked(boolean ischecked) {
			this.ischecked = ischecked;
		}




		// 是否是图片项

		public boolean isImage() {
			if (TextUtils.isEmpty(localPath)) {
				return false;
			}
			return true;
		}
		

		public boolean eqImageText(BaseImgTextItem item) {
			if (null == item) {
				return false;
			}
			if (style != item.style) {
				return false;
			}

			if (style > 1) {
				if (!TextUtils.isEmpty(item.copyName)) {

					if (!TextUtils.isEmpty(copyName)) {
						if (!item.copyName.equals(copyName)) {
							return false;
						}
					} else {
						if (!item.copyName.equals(FileUtil.getFileName(localPath))) {
							return false;
						}
					}

				} else {
					if (!TextUtils.isEmpty(copyName)) {
						if (!FileUtil.getFileName(item.localPath).equals(copyName)) {
							return false;
						}
					} else {
						if (!FileUtil.getFileName(item.localPath).equals(FileUtil.getFileName(localPath))) {
							return false;
						}
					}
				}

			} else {
				if (!TextUtils.isEmpty(textdesc) && !TextUtils.isEmpty(item.textdesc)) {
					if (!TextUtils.equals(textdesc, item.textdesc)) {
						return false;
					}
				}

			}

			return true;
		}


	public   BaseImgTextItem  Copy(){
		TempBaseVo imageTextsVo = new TempBaseVo();
		imageTextsVo.copyName = TextUtils.isEmpty(copyName) ? FileUtil.getFileName(localPath) : copyName;
		imageTextsVo.sort = sort;
		imageTextsVo.style = style;
		imageTextsVo.localPath = localPath;
		imageTextsVo.textdesc = textdesc;
		return  imageTextsVo;
	}


}
