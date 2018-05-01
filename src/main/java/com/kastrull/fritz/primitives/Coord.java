package com.kastrull.fritz.primitives;

import java.util.Objects;

import com.kastrull.fritz.Laws;

public final class Coord implements Approx<Coord> {

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
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Coord other = (Coord) obj;
		return Objects.equals(x, other.x) &&
				Objects.equals(y, other.y);
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ")";
	}

	public Coord mult(double z) {
		return Coord.c(x * z, y * z);
	}

	public double distansSqr() {
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

	public Coord xConjugate() {
		return c(-x, y);
	}

	public double distance() {
		return Math.sqrt(distansSqr());
	}

	public Coord rotate(double radians) {
		double cos = Math.cos(radians);
		double sin = Math.sin(radians);

		double xPrim = x * cos - y * sin;
		double yPrim = x * sin + y * cos;

		return c(xPrim, yPrim);
	}

	@Override
	public boolean approxEq(Coord c) {
		double deltaSqr = subtract(c).distansSqr();
		return -EPSILON_SQR < deltaSqr && deltaSqr < EPSILON_SQR;
	}

	public double dotProduct(Coord d) {
		return x * d.x + y * d.y;
	}

	public Coord negate() {
		return c(-x, -y);
	}

}
