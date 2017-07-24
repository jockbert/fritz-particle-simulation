package com.kastrull.fritz;

public interface SimState {

	static SimState create(double d, double e) {
		return new SimState() {

			@Override
			public SimState addParticle(double d, double e, double f, double g) {
				return this;
			}

			@Override
			public double wallAbsorbedMomentum() {
				return 0;
			}
		};
	}

	SimState addParticle(double d, double e, double f, double g);

	double wallAbsorbedMomentum();
}
