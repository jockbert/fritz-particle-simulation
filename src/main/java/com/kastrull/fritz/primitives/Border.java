package com.kastrull.fritz.primitives;

import java.util.Objects;

public class Border {

	/** Vertical wall, if x-axis is horizontal. */
	public static final boolean BY_X = true;

	/** Horizontal wall, if y-axis is vertical. */
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
		return Objects.hash(at, byX);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Border other = (Border) obj;
		return Objects.equals(at, other.at) &&
				byX == other.byX;
	}

	@Override
	public String toString() {
		return "Border(" + at + " " + (byX ? "|" : "-") + ")";
	}
}
