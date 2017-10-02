package com.kastrull.fritz.sim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Particle;

/** Particle register */
class Register {
	final List<Particle> particles;
	final double[] times;

	Register(List<Particle> particles, double initialTime) {
		this.particles = new ArrayList<>(particles);
		this.times = new double[particles.size()];
		Arrays.fill(times, initialTime);
	}

	public void modify(Integer pid, double atTime, Function<Particle, Particle> action) {

		Particle particleAtTime = getAtTime(pid, atTime);

		particles.set(pid, action.apply(particleAtTime));
		times[pid] = atTime;
	}

	public Particle getAtTime(Integer pid, double atTime) {
		double timeDiff = atTime - times[pid];
		return particles.get(pid).moveTime(timeDiff);
	}

	Stream<Integer> ids() {
		return IntStream.range(0, particles.size()).boxed();
	}

	public List<Particle> toList(double atTime) {
		List<Particle> result = new ArrayList<>(particles.size());
		for (int pid = 0; pid < particles.size(); pid++) {
			result.add(getAtTime(pid, atTime));
		}
		return result;
	}
}