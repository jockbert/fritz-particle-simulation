package com.kastrull.fritz.physics;

import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static com.kastrull.fritz.primitives.WallInteraction.wi;

import java.util.Optional;
import java.util.function.Predicate;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Interaction;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WallInteraction;

public final class LinearPhysics implements Physics {

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

		double a = diff.vel.distansSqr();
		double b = diff.pos.x * diff.vel.x + diff.pos.y * diff.vel.y;
		double c = diff.pos.distansSqr() - 4;

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

	@Override
	public Interaction interact(Particle before1, Particle before2) {
		Coord d = unitCollisionVector(before1, before2);
		double a = netExchangeOfMomentum(d, before1, before2);

		Coord velocityChange = d.mult(a); // works because mass is 1

		Particle after1 = before1.addVelocity(velocityChange);
		Particle after2 = before2.addVelocity(velocityChange.negate());
		return Interaction.i(after1, after2);
	}

	private double netExchangeOfMomentum(Coord d, Particle before1, Particle before2) {
		Coord momentum1 = before1.vel; // mass is 1
		Coord momentum2 = before2.vel; // mass is 1
		return momentum2.dotProduct(d) - momentum1.dotProduct(d);
	}

	private Coord unitCollisionVector(Particle before1, Particle before2) {
		Coord collision = before1.pos.subtract(before2.pos);
		double length = collision.distance();
		return collision.mult(1 / length);
	}

	@Override
	public WallInteraction interactWall(Particle p, Border wall) {
		Coord wallMomentum;
		Particle newP;
		if (wall.byX) {
			wallMomentum = c(p.vel.x * 2, 0.0);
			newP = p(p.pos, p.vel.xConjugate());
		} else {
			wallMomentum = c(0.0, p.vel.y * 2);
			newP = p(p.pos, p.vel.yConjugate());
		}
		return wi(newP, wallMomentum);
	}

	@Override
	public Optional<Double> collisionTime(Particle p, Border wall) {

		double velocity = wall.byX ? p.vel.x : p.vel.y;
		double distance = wall.at - (wall.byX ? p.pos.x : p.pos.y);
		double time = distance / velocity;

		boolean isInPresentOrNearFuture = time >= 0 && Double.isFinite(time);

		return isInPresentOrNearFuture ? Optional.of(time) : Optional.empty();
	}
}
