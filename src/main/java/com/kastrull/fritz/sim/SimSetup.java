package com.kastrull.fritz.sim;

import com.kastrull.fritz.primitives.Coord;

public class SimSetup {

	static SimSetup ss(Coord size, int particleCount, double maxSpeed, double rndSeed, double simTime, String name) {
		return new SimSetup(size, particleCount, maxSpeed, rndSeed, simTime, name);
	}

	public final Coord size;
	public final int particleCount;
	public final double maxSpeed;
	public final double rndSeed;
	public final double simTime;
	public final String name;

	public SimSetup(Coord size, int particleCount, double maxSpeed, double rndSeed, double simTime, String name) {
		this.size = size;
		this.particleCount = particleCount;
		this.maxSpeed = maxSpeed;
		this.rndSeed = rndSeed;
		this.simTime = simTime;
		this.name = name;
	}
}
