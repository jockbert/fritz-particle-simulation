package com.kastrull.fritz.sim;

import com.kastrull.fritz.primitives.Particle;

/** A mutable handle/reference for a particle at a given time. */
public class SimParticle {

	private Particle particle;
	private double atTime;

	public SimParticle(Particle particle, double atTime) {
		this.particle = particle;
		this.atTime = atTime;
	}

	Particle getAtTime(double atTime) {
		double timeDiff = atTime - this.atTime;
		return particle.moveTime(timeDiff);
	}

	void modify(Particle particle, double atTime) {
		this.atTime = atTime;
		this.particle = particle;
	}
}
