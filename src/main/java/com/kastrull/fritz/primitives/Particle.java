package com.kastrull.fritz.primitives;

import java.util.Objects;

import com.kastrull.fritz.Laws;

public class Particle implements Approx<Particle> {

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
		return Objects.hash(pos, vel);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Particle other = (Particle) obj;
		return Objects.equals(pos, other.pos) &&
				Objects.equals(vel, other.vel);
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

	@Override
	public boolean approxEq(Particle p) {
		return pos.approxEq(p.pos)
				&& vel.approxEq(p.vel);
	}

	public Particle rotate(double radians) {
		return p(pos.rotate(radians), vel.rotate(radians));
	}

	public double distanceAbs() {
		return Math.sqrt(pos.distansSqr() + vel.distansSqr());
	}

	public Particle moveTime(double time) {
		return move(vel.mult(time));
	}

	public double distancePos(Particle q) {
		return pos.subtract(q.pos).distance();
	}

	public Particle addVelocity(Coord velocityChange) {
		return p(pos, vel.add(velocityChange));
	}

	public boolean isOverlapping(Particle p2) {
		return pos.subtract(p2.pos).distance() <= Laws.PARTICLE_DIAMETER;
	}
}
