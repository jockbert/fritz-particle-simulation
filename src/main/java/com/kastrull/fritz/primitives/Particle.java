package com.kastrull.fritz.primitives;

public class Particle {

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
}
