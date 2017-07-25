package com.kastrull.fritz.sim;

import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;

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

			@Override
			public Stream<Border> walls() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Stream<Particle> particles() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public double targetTime() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public double currentTime() {
				// TODO Auto-generated method stub
				return 0;
			}
		};
	}

	SimState addParticle(double d, double e, double f, double g);

	double wallAbsorbedMomentum();

	Stream<Border> walls();

	Stream<Particle> particles();

	double targetTime();

	double currentTime();
}
