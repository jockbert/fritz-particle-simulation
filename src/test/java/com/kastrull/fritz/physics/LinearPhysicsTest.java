package com.kastrull.fritz.physics;

import static com.kastrull.fritz.primitives.Border.b;
import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.Test;
import org.quicktheories.quicktheories.core.Source;

import com.kastrull.fritz.Laws;
import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Interaction;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WallInteraction;
import com.kastrull.fritz.primitives.WithAssert;
import com.kastrull.fritz.primitives.WithQtAndPrimitives;

public class LinearPhysicsTest implements WithQtAndPrimitives, WithAssert {

	Physics phy = new LinearPhysics();

	@Test
	public void collisionTime_WithOtherParticle() {
		qt()
			.forAll(
				boxedParticles(),
				boxedParticles())
			.checkAssert((a, b) -> {
				Optional<Double> time = phy.collisionTime(a, b);
				assertNotNull(time);
				boolean isValidTime = !time.orElseGet(() -> 0.0).isNaN();
				assertTrue(hasNoCollision(time) || isValidTime);
			});
	}

	@Test
	public void collisionTime_verticalExample() {
		Particle p1 = p(c(0, 2), c(0, -40));
		Particle p2 = p(c(0, -80), c(0, 0));

		Optional<Double> actualTime = phy.collisionTime(p1, p2);
		Double expectedTime = 2.0;

		assertTrue(actualTime.isPresent());
		assertApprox(expectedTime, actualTime.get());
	}

	@Test
	public void collisionTime_touchingTwins() {
		qt()
			.forAll(
				particles(),
				twoRadiousDistances())
			.assuming((p, distance) -> p.isFinite())
			.check((p, distance) -> hasNoCollision(phy.collisionTime(p, p.move(distance))));
	}

	@Test
	public void collisionTime_neverCollide() {
		qt()
			.forAll(boxedParticles())
			.assuming(p -> p.pos.y > Laws.PARTICLE_RADIUS)
			.assuming(p -> p.vel.y > 0)
			.check(p -> hasNoCollision(
				phy.collisionTime(p, p.yConjugate())));
	}

	@Test
	public void collisionTime_alwaysCollide() {
		qt()
			// On y-axis positive position and negative velocity.
			.forAll(boxedParticles())
			.assuming(p -> p.pos.y > Laws.PARTICLE_RADIUS)
			.assuming(p -> p.vel.y < 0)
			.check(p -> hasCollision(phy.collisionTime(p, p.yConjugate())));
	}

	@Test
	public void collisionTime_moveParticle_roundtrip() {
		qt()
			// On y-axis positive position and negative velocity.
			.forAll(boxedParticles())
			.assuming(p -> p.pos.y > Laws.PARTICLE_RADIUS)
			.assuming(p -> p.vel.y < 0)
			.checkAssert(p1 -> {

				Particle p2 = p1.yConjugate();

				Optional<Double> collisionTime = phy.collisionTime(p1, p2);

				assertTrue(collisionTime.isPresent());

				collisionTime.ifPresent(time -> {
					Particle q1 = p1.moveTime(time);
					Particle q2 = p2.moveTime(time);

					double distance = q1.posDistance(q2);
					assertEquals(Laws.PARTICLE_DIAMETER, distance, 0.001);
				});
			});
	}

	@Test
	public void interaction_preserveEnergy() {
		qt()
			.forAll(
				boxedParticles(),
				boxedParticles())
			.checkAssert((p, q) -> {

				Interaction i = phy.interact(p, q);
				double energyBefore = totalEnergy(p, q);
				double energyAfter = totalEnergy(i.p1, i.p2);

				assertApprox("energy", energyBefore, energyAfter);
			});
	}

	private double totalEnergy(Particle... ps) {
		return Stream.of(ps)
			.map(p -> p.vel.dotProduct(p.vel) / 2)
			.reduce(0.0, (a, b) -> a + b);
	}

	@Test
	public void interaction_preserveMomentum() {
		qt()
			.forAll(
				boxedParticles(),
				boxedParticles())
			.checkAssert((p, q) -> {

				Interaction i = phy.interact(p, q);
				Coord momentumBefore = totalMomentum(p, q);
				Coord momentumAfter = totalMomentum(i.p1, i.p2);

				assertApprox("momentum", momentumBefore, momentumAfter);
			});
	}

	private Coord totalMomentum(Particle... ps) {
		return Stream.of(ps)
			.map(p -> p.vel)
			.reduce(Coord.ZERO, (a, b) -> a.add(b));
	}

	@Test
	public void interact_exampleHeadOnHit() {
		Particle before1 = p(c(0, 0), c(2, 0));
		Particle before2 = p(c(3, 0), c(0, 1));

		Particle after1 = p(c(0, 0), c(0, 0));
		Particle after2 = p(c(3, 0), c(2, 1));

		Interaction expected = Interaction.i(after1, after2);
		Interaction actual = phy.interact(before1, before2);

		assertNotNull(expected);
		assertNotNull(actual);

		assertEquals(expected, actual);
	}

	@Test
	public void interact_exampleOnlyTouch() {
		Particle before1 = p(c(0, 0), c(2, 0));
		Particle before2 = p(c(0, 1), c(-1, 0));

		Particle after1 = p(c(0, 0), c(2, 0));
		Particle after2 = p(c(0, 1), c(-1, 0));

		Interaction expected = Interaction.i(after1, after2);
		Interaction actual = phy.interact(before1, before2);

		assertNotNull(expected);
		assertNotNull(actual);
		assertEquals(expected, actual);
	}

	@Test
	public void interact_examplePartialHit() {
		Particle before1 = p(c(0, 0), c(1, 0));
		Particle before2 = p(c(1, 1), c(0, 0));

		Particle after1 = p(c(0, 0), c(0.5, -0.5));
		Particle after2 = p(c(1, 1), c(0.5, 0.5));

		Interaction expected = Interaction.i(after1, after2);
		Interaction actual = phy.interact(before1, before2);

		assertNotNull(expected);
		assertNotNull(actual);
		assertApprox(expected, actual);
	}

	@Test
	public void collisionTimeWall_examples() {

		Border wall = b(4, Border.BY_X);
		Coord start = c(0, 0);

		Particle pos = p(start, c(1, 2));
		Particle zero = p(start, c(0, 2));
		Particle neg = p(start, c(-1, 2));
		Particle slow = p(start, c(1e-100, 2));
		Particle fast = p(start, c(1e100, -2));

		assertApprox(4.0, phy.collisionTime(pos, wall));
		assertNone(phy.collisionTime(zero, wall));
		assertNone(phy.collisionTime(neg, wall));
		assertApprox(4.0e100, phy.collisionTime(slow, wall));
		assertApprox(4.0e-100, phy.collisionTime(fast, wall));

		assertApprox(2.0, phy.collisionTime(pos, b(4, Border.BY_Y)));
		assertApprox(4.0, phy.collisionTime(neg, b(-4, Border.BY_X)));
	}

	@Test
	public void interactWall_preserveMomentum() {
		qt()
			.forAll(
				boxedBorders(),
				boxedParticles())
			.checkAssert((wall, p) -> {
				WallInteraction wi = phy.interactWall(p, wall);

				Coord momBefore = totalMomentum(p);
				Coord momAfter = totalMomentum(wi.p).add(wi.wallMomentum);
				assertApprox(momBefore, momAfter);
			});
	}

	@Test
	public void interactWall_exampleByX() {
		Border wall = Border.b(4, Border.BY_X);
		Particle p = p(c(0, 0), c(1, 2));

		Particle expectedP = p(c(0, 0), c(-1, 2));
		Coord expectedMom = c(2, 0);

		WallInteraction wi = phy.interactWall(p, wall);
		Particle actualP = wi.p;
		Coord actualMom = wi.wallMomentum;

		assertApprox(expectedP, actualP);
		assertApprox(expectedMom, actualMom);
	}

	@Test
	public void interactWall_exampleByY() {
		Border wall = Border.b(4, Border.BY_Y);
		Particle p = p(c(0, 0), c(1, 2));

		Particle expectedP = p(c(0, 0), c(1, -2));
		Coord expectedMom = c(0, 4);

		WallInteraction wi = phy.interactWall(p, wall);
		Particle actualP = wi.p;
		Coord actualMom = wi.wallMomentum;

		assertApprox(expectedP, actualP);
		assertApprox(expectedMom, actualMom);
	}

	private boolean hasCollision(Optional<Double> collisionTime) {
		return collisionTime.isPresent();
	}

	private boolean hasNoCollision(Optional<Double> collisionTime) {
		return !collisionTime.isPresent();
	}

	private Source<Coord> twoRadiousDistances() {
		Source<Coord> twoRadious = arbitrary()
			.pick(c(0, 2), c(0, -2), c(2, 0), c(-2, 0));
		return twoRadious;
	}

	@Test
	public void positionAtTime_example() {
		Particle p = p(c(-1, -2), c(4, 40));
		double time = 1.5;
		Coord actualPos = phy.posAtTime(p, time);

		Coord expectedPos = c(4 * 1.5 - 1, 40 * 1.5 - 2);

		assertEquals(expectedPos, actualPos);
	}
}
