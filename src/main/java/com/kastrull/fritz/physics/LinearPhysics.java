package com.kastrull.fritz.physics;

import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static com.kastrull.fritz.primitives.WallInteraction.wi;

import java.util.Optional;
import java.util.function.Predicate;

import com.kastrull.fritz.Laws;
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

		// Only care about decreasing distance between particles
		if (!isDistanceDecreasing(p1, p2))
			return Optional.empty();

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

	/**
	 * TODO better explanation in this comment
	 *
	 * Visible for testing.
	 *
	 * let a(t) be first particles x-position as expression of time
	 *
	 * let b(t) be second particles x-position as expression of time
	 *
	 * let f(t) be first particles y-position as expression of time
	 *
	 * let g(t) be second particles y-position as expression of time
	 *
	 * abs(t) = sqrt((a(t)-b(t))^2+(f(t)-g(t))^2)
	 *
	 * abs^2(t) = (a(t)-b(t))^2+(f(t)-g(t))^2
	 *
	 * (abs^2)'(t) = 2(a(t)-b(t))(a'(t)-b'(x)) + 2(f(t)-g(t))(f'(t)-g'(t))
	 *
	 * a(t) = a.pos.x + a.vel.x * t => a(0) = a.pos.x
	 *
	 * a'(t) = a.vel.x => a'(0) = a.vel.x
	 *
	 * distanceChangePow2Times2 = posDiff.x * velDiff.x + posDiff.y * velDiff.y
	 */
	boolean isDistanceDecreasing(Particle a, Particle b) {

		Coord posDiff = a.pos.subtract(b.pos);
		Coord velDiff = a.vel.subtract(b.vel);

		double distanceChangePow2Times2 = posDiff.x * velDiff.x
				+ posDiff.y * velDiff.y;

		return distanceChangePow2Times2 < 0;
	}

	private double netExchangeOfMomentum(Coord d, Particle before1, Particle before2) {
		Coord momentum1 = before1.vel; // XXX mass is 1
		Coord momentum2 = before2.vel; // XXX mass is 1
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
	public Optional<Double> collisionTimeWall(Particle p, Border wall) {

		double velocity = wall.byX ? p.vel.x : p.vel.y;
		double distance = wall.at - (wall.byX ? p.pos.x : p.pos.y);

		// compensate for particle radius
		double distanceMinusRadius = distance + (distance < 0 ? 1 : -1) * Laws.PARTICLE_RADIUS;

		// distance and velocity has different signs.
		boolean isMovingAway = distance * velocity < 0;

		// distance to wall is less than a particle radius - hence overlap.
		boolean isOverlapping = Math.abs(distance) < Laws.PARTICLE_RADIUS;

		if (isMovingAway || isOverlapping) {
			return Optional.empty();
		}

		double time = distanceMinusRadius / velocity;

		boolean isInPresentOrNearFuture = time >= 0 && Double.isFinite(time);

		return isInPresentOrNearFuture ? Optional.of(time) : Optional.empty();
	}
}
