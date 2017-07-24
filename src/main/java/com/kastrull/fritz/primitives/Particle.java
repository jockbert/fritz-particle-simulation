package com.kastrull.fritz.primitives;

public class Particle {

	public static final Particle ZERO = p(Coord.ZERO, Coord.ZERO);
	public static final Particle UNIT = p(Coord.UNIT, Coord.UNIT);
	public static final double EPSILON = Coord.EPSILON * 2;

	public final Coord pos;
	public final Coord vel;

	public Particle(Coord position, Coord velocity) {
		pos = position;
		vel = velocity;
	}

	public static Particle p(Coord position, Coord velocity) {
		return new Particle(position, velocity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pos == null) ? 0 : pos.hashCode());
		result = prime * result + ((vel == null) ? 0 : vel.hashCode());
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
		Particle other = (Particle) obj;
		if (pos == null) {
			if (other.pos != null)
				return false;
		} else if (!pos.equals(other.pos))
			return false;
		if (vel == null) {
			if (other.vel != null)
				return false;
		} else if (!vel.equals(other.vel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Particle [pos=" + pos + ", vel=" + vel + "]";
	}

	public Particle subtract(Particle z) {
		return p(pos.subtract(z.pos), vel.subtract(z.vel));
	}

	public boolean isFinite() {
		return pos.isFinite() && vel.isFinite();
	}

	public Particle move(Coord distance) {
		return p(pos.add(distance), vel);
	}

	public Particle yConjugate() {
		return p(pos.yConjugate(), vel.yConjugate());
	}

	public boolean approxEq(Particle p) {
		return pos.approxEq(p.pos)
				&& vel.approxEq(p.vel);
	}

	public Particle rotate(double radians) {
		return p(pos.rotate(radians), vel.rotate(radians));
	}

	public double distance() {
		return Math.sqrt(pos.absSqr() + vel.absSqr());
	}

	public Particle moveTime(double time) {
		return move(vel.mult(time));
	}

	public double posDistance(Particle q) {
		return pos.subtract(q.pos).abs();
	}

	public Particle addVelocity(Coord velocityChange) {
		return p(pos, vel.add(velocityChange));
	}
}
