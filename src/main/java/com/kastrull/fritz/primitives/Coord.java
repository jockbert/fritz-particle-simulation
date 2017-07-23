package com.kastrull.fritz.primitives;

import com.kastrull.fritz.Laws;

public final class Coord {

	public static final double EPSILON = Laws.EPSILON * 2;
	private static final double EPSILON_SQR = EPSILON * EPSILON;
	public static final Coord ZERO = c(0, 0);
	public static final Coord UNIT = c(1, 1);
	public final double x;
	public final double y;

	public Coord(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/** Creation method. */
	public static Coord c(double x, double y) {
		return new Coord(x, y);
	}

	public Coord add(Coord b) {
		return c(x + b.x, y + b.y);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
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
		Coord other = (Coord) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public Coord mult(double z) {
		return Coord.c(x * z, y * z);
	}

	public double absSqr() {
		return (x * x) + (y * y);
	}

	public Coord subtract(Coord z) {
		return c(x - z.x, y - z.y);
	}

	public boolean isFinite() {
		return Double.isFinite(x) && Double.isFinite(y);
	}

	public Coord yConjugate() {
		return c(x, -y);
	}

	public double abs() {
		return Math.sqrt(absSqr());
	}

	public Coord rotate(double radians) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);

		double xPrim = x * cos - y * sin;
		double yPrim = x * sin + y * cos;

		return c(xPrim, yPrim);
	}

	public boolean approxEq(Coord c) {
		double DELTA_SQR = subtract(c).absSqr();
		return -EPSILON_SQR < DELTA_SQR && DELTA_SQR < EPSILON_SQR;
	}
}
