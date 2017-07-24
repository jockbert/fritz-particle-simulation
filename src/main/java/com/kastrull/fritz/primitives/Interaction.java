package com.kastrull.fritz.primitives;

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((p1 == null) ? 0 : p1.hashCode());
		result = prime * result + ((p2 == null) ? 0 : p2.hashCode());
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
		Interaction other = (Interaction) obj;
		if (p1 == null) {
			if (other.p1 != null)
				return false;
		} else if (!p1.equals(other.p1))
			return false;
		if (p2 == null) {
			if (other.p2 != null)
				return false;
		} else if (!p2.equals(other.p2))
			return false;
		return true;
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
