package com.kastrull.fritz.sim;

import java.util.List;

import com.kastrull.fritz.physics.Physics;
import com.kastrull.fritz.primitives.Border;

public final class Context {
	public final Physics phy;
	public final Register register;
	public final List<Border> walls;
	public double atTime;
	public double wallAbsorbedMomentum;

	Context(Physics phy, Register register, List<Border> walls, double atTime, double wallAbsorbedMomentum) {
		this.phy = phy;
		this.register = register;
		this.walls = walls;
		this.atTime = atTime;
		this.wallAbsorbedMomentum = wallAbsorbedMomentum;
	}
}