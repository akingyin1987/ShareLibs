package org.easydarwin.video.beautify.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.easydarwin.video.beautify.conf.Conf;
import org.easydarwin.video.beautify.model.VideoBeautifyTheme;
import org.easydarwin.video.beautify.task.VideoProcessor;
import org.xmlpull.v1.XmlPullParser;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Xml;

public class VideoBeautifyUtil {

	private static VideoBeautifyUtil mInstance;

	public static VideoBeautifyUtil getInstance() {
		if (mInstance == null) {
			mInstance = new VideoBeautifyUtil();
		}
		return mInstance;
	}

	String assets[] = { "config.xml", "filter/", "theme/" };

	public void init(final Context ctx) {
		ContextHolder.getInstance().setContext(ctx);
		final File external = ctx.getExternalFilesDir(null);
		if (external == null) {
			return;
		}
		SharedPreferences preferencess = ctx.getSharedPreferences("VideoBeautifyInitResult", Context.MODE_PRIVATE);
		String version = preferencess.getString("versionresult", "1.0");

		if (!Conf.V.equals(version)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					for (final String string : assets) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								realeasAssets(ctx, string, ProjectUtils.getResourcePath());
							}
						}).start();
					}
					SharedPreferences preferences = ctx.getSharedPreferences("VideoBeautifyInitResult", Context.MODE_PRIVATE);
					Editor editor = preferences.edit();
					editor.putString("versionresult", Conf.V);
					editor.commit();
				}
			}).start();
		}
		return;
	}

	public static void deleteFileDir(File dir) {
		final File f = dir;
		try {
			if (f.exists() && f.isDirectory()) {
				File[] delFiles = f.listFiles();
				if (delFiles == null || delFiles.length == 0) {
					f.delete();
				} else {
					int len = delFiles.length;
					for (int j = 0; j < len; j++) {
						if (delFiles[j].isDirectory()) {
							deleteFileDir(delFiles[j]);
						} else {
							delFiles[j].delete();// 删除文件
						}
					}
					delFiles = null;
				}
				deleteFileDir(f);// 递归调用
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<VideoBeautifyTheme> parse(String item, String imagepath) {
		String itemflag = "1";
		if (item.equals("filter")) {
			itemflag = "2";
		}
		if (item.equals("frame")) {
			itemflag = "4";
		}
		if (item.equals("bubble")) {
			itemflag = "5";
		}
		if (item.equals("font")) {
			itemflag = "6";
		}
		if (item.equals("cartoon")) {
			itemflag = "8";
		}
		if (item.equals("music")) {
			itemflag = "3";
		}
		if (item.equals("sound")) {
			itemflag = "10";
		}
		List<VideoBeautifyTheme> videobeautifylist = null;
		VideoBeautifyTheme videobeautify = null;
		InputStream stream = null;
		try {
			stream = ContextHolder.getInstance().getContext().getAssets().open("config.xml");
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(stream, "UTF-8");
			String image = "";
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
					case XmlPullParser.START_DOCUMENT:
						videobeautifylist = new ArrayList<VideoBeautifyTheme>();
						break;
					case XmlPullParser.START_TAG:
						if (parser.getName().equals("item")) {
							videobeautify = new VideoBeautifyTheme();
						} else if (parser.getName().equals("id")) {
							eventType = parser.next();
							videobeautify.setId(parser.getText());
						} else if (parser.getName().equals("name")) {
							eventType = parser.next();
							videobeautify.setName(parser.getText());
						} else if (parser.getName().equals("type")) {
							eventType = parser.next();
							videobeautify.setType(parser.getText());
						} else if (parser.getName().equals("image")) {
							eventType = parser.next();
							videobeautify.setImagepath(parser.getText());
						} else if (parser.getName().equals("download")) {
							eventType = parser.next();
							videobeautify.setDownload(parser.getText());
						}
						break;
					case XmlPullParser.END_TAG:
						if (parser.getName().equals("item")) {
							if (videobeautify.getType().equals(itemflag)) {
								Bitmap bm = null;
								image = videobeautify.getImagepath();
								if (image != "" && image != null) {
									try {
										bm = BitmapFactory.decodeStream(ContextHolder
											.getInstance()
											.getContext()
											.getAssets()
											.open(item + "/" + videobeautify.getId() + "/" + image));
										videobeautify.setImage(bm);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								videobeautifylist.add(videobeautify);
							}
							videobeautify = null;
						}
						break;
				}
				eventType = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return videobeautifylist;
	}

	private int realeasAssets(Context context, String assetSrc, String desDir) {
		AssetManager assetManager = context.getAssets();
		try {
			if (TextUtils.isEmpty(assetSrc)) {
				return 0;
			}
			if (!desDir.endsWith("/")) {
				desDir += "/";
			}
			if (TextUtils.isEmpty(assetSrc) || assetSrc.equals("/")) {
				assetSrc = "";
			} else if (assetSrc.endsWith("/")) {
				assetSrc = assetSrc.substring(0, assetSrc.length() - 1);
			}
			String assets[] = assetManager.list(assetSrc);
			if (assets.length > 0) {//dir
				for (String name : assets) {
					if (!TextUtils.isEmpty(name)) {
						name = assetSrc + "/" + name;//补全assets资源路径
					}
					String[] childNames = assetManager.list(name);
					if (!TextUtils.isEmpty(name) && childNames.length > 0) {
						realeasAssets(context, name, desDir);//递归
					} else {
						copyFile(context, name, desDir);
					}
				}
			} else {
				copyFile(context, assetSrc, desDir);
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		return 1;
	}

	private void copyFile(final Context context, final String filename, final String desDir) {
		AssetManager assetManager = context.getAssets();
		InputStream in = null;
		OutputStream out = null;
		try {
			in = assetManager.open(filename);
			String newFileName = desDir + filename;
			File file = new File(newFileName);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
			}
			out = new FileOutputStream(newFileName);
			byte[] buffer = new byte[1024 * 2];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return;
	}

	public void finishFilter() {
		if (ContextHolder.getInstance().getActivity() != null) {
			ContextHolder.getInstance().getActivity().finish();
		}
		VideoCommonPara.mInstance = null;
	}

	public void setFilterListener(VideoProcessor.FilterListener L) {
		VideoProcessor.setFilterL(L);
	}
}
