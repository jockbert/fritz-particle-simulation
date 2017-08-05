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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(at);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + (byX ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Border other = (Border) obj;
		if (Double.doubleToLongBits(at) != Double.doubleToLongBits(other.at))
			return false;
		return byX == other.byX;
	}

	@Override
	public String toString() {
		return "Border(" + at + " " + (byX ? "|" : "-") + ")";
	}
}
