package com.kastrull.fritz.primitives;

public class WallInteraction {

	public final Particle p;
	public final Coord wallMomentum;

	public WallInteraction(Particle p, Coord wallMomentum) {
		this.p = p;
		this.wallMomentum = wallMomentum;
	}

	public static WallInteraction wi(Particle p, Coord wallMomentum) {
		return new WallInteraction(p, wallMomentum);
	}
}
