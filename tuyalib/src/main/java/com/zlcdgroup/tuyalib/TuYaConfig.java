package com.zlcdgroup.tuyalib;

import java.util.ArrayList;
import java.util.List;

public class TuYaConfig {

	private static List<Shape> arrowiconList = new ArrayList<Shape>();

	private static List<Shape> lineiconList = new ArrayList<Shape>();

	private static List<Shape> mlineiconList = new ArrayList<Shape>();

	private static int width;

	private static int height;

	public static void initIcon(int width, int height) {
		if (TuYaConfig.width == width && TuYaConfig.height == height) {
			return;
		}
		TuYaConfig.width = width;
		TuYaConfig.height = height;
		arrowiconList.clear();
		lineiconList.clear();
		mlineiconList.clear();
		for (int i = 0; i < 24; i++) {
			if (width >= height) {

				height = 200 * height / width;
				width = 200;
			} else {
				width = 200 * width / height;
				height = 200;
			}
			Arrow arrow = new Arrow(width, height, i * 15, 0.5, 0.5);
			arrow.reset(0.6, 0.6);
			arrowiconList.add(arrow);

			Line line = new Line(width, height, i * 15, 0.5, 0.5);
			line.reset(0.6, 0.6);
			lineiconList.add(line);
		}

		for (int i = 13; i < 24; i++) {
			if (i <= 18) {
				MLine mLine = new MLine(width, height, i * 15, 1.8, false);
				mLine.reset(0.3, 0.3);
				mlineiconList.add(mLine);
			} else if (i >= 18) {
				MLine leftMLine = new MLine(width, height, i * 15, 1.8, true);
				mlineiconList.add(leftMLine);
			    leftMLine.reset(0.3, 0.3);
			}
		}

	}

	public static List<Shape> getArrowIcon() {

		return arrowiconList;
	}

	public static List<Shape> getLineIcon() {
		return lineiconList;

	}

	public static List<Shape> getMlineIcon() {
		return mlineiconList;
	}

}
