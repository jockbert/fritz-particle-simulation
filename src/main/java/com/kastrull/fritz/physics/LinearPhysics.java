package com.kastrull.fritz.physics;

import java.util.Optional;
import java.util.function.Predicate;

import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;

public class LinearPhysics implements Physics {

	/**
	 * The routine collide returns no value if there will be no collision,
	 * otherwise it will return when in time the collision occurs.
	 */
	@Override
	public Optional<Double> collisionTime(Particle p1, Particle p2) {

		// Solving polynomial of when in time there are 2 radius distances
		// between particles, i.e. collisions. Filtering out roots (solutions)
		// in negative time, i.e. time in history. Selects the first occurrence
		// in time if there are two.

		Particle diff = p1.subtract(p2);

		double a = diff.vel.absSqr();
		double b = diff.pos.x * diff.vel.x + diff.pos.y * diff.vel.y;
		double c = diff.pos.absSqr() - 4;

		Predicate<Double> isInPresentOrFuture = x -> x >= 0;

		return PolySolver
			.findRealRoots(a, b, c)
			.filter(Double::isFinite) // infinite and NaN answers are unreliable
			.filter(isInPresentOrFuture)
			.sorted()
			.findFirst();
	}

	@Override
	public Coord posAtTime(Particle p, double time) {
		return p.pos.add(p.vel.mult(time));
	}
}
