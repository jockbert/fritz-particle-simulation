package com.kastrull.fritz.primitives;

public class Border {

	public static final boolean BY_X = true;
	public static final boolean BY_Y = false;

	public final double at;
	public final boolean byX;

	public Border(double at, boolean byX) {
		this.at = at;
		this.byX = byX;
	}

	public static Border b(double at, boolean byX) {
		return new Border(at, byX);
	}
}
