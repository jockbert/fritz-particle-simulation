package com.kastrull.fritz.sim.event;

import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WallInteraction;
import com.kastrull.fritz.sim.Context;

public final class WallHit extends Event {

	public final Border wall;
	public final Integer pid;
	private final Context ctx;

	public WallHit(double atTime, Border wall, Integer pid, Context ctx) {
		super(atTime);
		this.wall = wall;
		this.pid = pid;
		this.ctx = ctx;
	}

	@Override
	public boolean apply() {
		Particle particle = ctx.register.getAtTime(pid, atTime());

		WallInteraction interaction = ctx.phy.interactWall(particle, wall);

		ctx.register.modify(pid, atTime(),
			particleIgnored -> interaction.p);

		ctx.wallAbsorbedMomentum += interaction.wallMomentum.distance();

		return false;
	}

	@Override
	public Stream<Integer> getPids() {
		return Stream.of(pid);
	}
}