package com.kastrull.fritz.sim;

import com.kastrull.fritz.primitives.Coord;

public class SimSetup {

	public static final SimSetup NULL = ss(Coord.ZERO, 0, 0, 0, 0, "");

	public static SimSetup ss(Coord size, int particleCount, double maxSpeed, double simTime, long rndSeed,
			String name) {
		return new SimSetup(size, particleCount, maxSpeed, simTime, rndSeed, name);
	}

	public final Coord size;
	public final int particleCount;
	public final double maxSpeed;
	public final double simTime;
	public final long rndSeed;
	public final String name;

	public SimSetup(Coord size, int particleCount, double maxSpeed, double simTime, long rndSeed, String name) {
		this.size = size;
		this.particleCount = particleCount;
		this.maxSpeed = maxSpeed;
		this.simTime = simTime;
		this.rndSeed = rndSeed;
		this.name = name;
	}

	@Override
	public String toString() {
		return "SimSetup(size=" + size + ", particleCount=" + particleCount + ", maxSpeed=" + maxSpeed + ", simTime="
				+ simTime + ", rndSeed=" + rndSeed + ", name=" + name + ")";
	}

}
