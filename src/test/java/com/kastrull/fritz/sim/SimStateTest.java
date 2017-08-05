package com.kastrull.fritz.sim;

import static com.kastrull.fritz.primitives.Coord.c;
import static com.kastrull.fritz.primitives.Particle.p;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import org.quicktheories.quicktheories.WithQuickTheories;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WithQtAndPrimitives;

public class SimStateTest implements WithQtAndPrimitives, WithSimSources, WithQuickTheories {

	private static final Border SOME_BORDER = Border.b(3.3, Border.BY_Y);
	private static final Border SOME_OTHER_BORDER = Border.b(SOME_BORDER.at, !SOME_BORDER.byX);
	private static final Coord SOME_VEL = c(2, 2);
	private static final Coord SOME_POS = c(1, 1);
	private static final Particle SOME_PARTICLE = p(SOME_POS, SOME_VEL);
	private static final Particle SOME_OTHER_PARTICLE = SOME_PARTICLE.addVelocity(Coord.UNIT);
	private static final double SOME_CURRENT_TIME = 9.9;
	private static final double SOME_TARGET_TIME = 4.4;
	private static final double SOME_MOMENTUM = 6.6;

	private SimState ss() {
		return SimState.NULL
			.addParticle(SOME_PARTICLE)
			.addWall(SOME_BORDER)
			.wallAbsorbedMomentum(SOME_MOMENTUM)
			.currentTime(SOME_CURRENT_TIME)
			.targetTime(SOME_TARGET_TIME);
	}

	@Test
	public void testEquals() {
		assertEquals(ss(), ss());
		assertNotEquals(ss(), ss().addParticle(SOME_OTHER_PARTICLE));
		assertNotEquals(ss(), ss().addWall(SOME_OTHER_BORDER));
		assertNotEquals(ss(), ss().wallAbsorbedMomentum(SOME_MOMENTUM + 1));
		assertNotEquals(ss(), ss().currentTime(SOME_CURRENT_TIME + 1));
		assertNotEquals(ss(), ss().targetTime(SOME_TARGET_TIME + 1));
		assertEquals(ss(), ss().targetTime(SOME_TARGET_TIME));
	}

	@Test
	public void testCurrentTime() throws Exception {
		qt()
			.forAll(
				doubles().fromNegativeInfinityToPositiveInfinity())
			.check(
				t -> t == SimState.NULL.currentTime(t).currentTime());
	}

	@Test
	public void testTargetTime() throws Exception {
		qt()
			.forAll(
				doubles().fromNegativeInfinityToPositiveInfinity())
			.check(
				t -> t == SimState.NULL.targetTime(t).targetTime());
	}

	@Test
	public void testWallAbsorbedMomentum() throws Exception {
		qt()
			.forAll(
				doubles().fromNegativeInfinityToPositiveInfinity())
			.check(
				m -> m == SimState.NULL.wallAbsorbedMomentum(m).wallAbsorbedMomentum());
	}

	@Test
	public void testParticles() throws Exception {
		qt()
			.forAll(lists()
				.allListsOf(boxedParticles())
				.ofSizeBetween(0, 100))
			.check(ps -> ps.equals(
				SimState.NULL.particles(ps).particles()));
	}

	@Test
	public void testWalls() throws Exception {
		qt()
			.forAll(lists()
				.allListsOf(boxedBorders())
				.ofSizeBetween(0, 100))
			.check(ws -> ws.equals(
				SimState.NULL.walls(ws).walls()));
	}
}
