package com.zlcdgroup.tuyalib;

public enum ShapeType {
	Arrow(1), Circle(2), Line(3), MLine(4), ArrowWithTxt(5),Rectangle(6),Mosaic(
			7),TurnPainAround(8), NULL(-1);
	private int type;

	public int getType() {
		return type;
	}

	private ShapeType(int type) {
		this.type = type;
	}
}
