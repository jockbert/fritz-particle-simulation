package com.kastrull.fritz.primitives;

import java.util.Objects;

public class Interaction implements Approx<Interaction> {

	public final Particle p1;
	public final Particle p2;

	public Interaction(Particle p1, Particle p2) {
		this.p1 = p1;
		this.p2 = p2;
	}

	public static Interaction i(Particle p1, Particle p2) {
		return new Interaction(p1, p2);
	}

	@Override
	public int hashCode() {
		return Objects.hash(p1, p2);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Interaction other = (Interaction) obj;
		return Objects.equals(p1, other.p1) &&
				Objects.equals(p2, other.p2);
	}

	@Override
	public String toString() {
		return "Interaction [p1=" + p1 + ", p2=" + p2 + "]";
	}

	@Override
	public boolean approxEq(Interaction other) {
		return p1.approxEq(other.p1) && p2.approxEq(other.p2);
	}
}
