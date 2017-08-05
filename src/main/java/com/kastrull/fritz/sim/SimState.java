package com.kastrull.fritz.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;

public final class SimState {

	public static final SimState NULL = new SimState(new ArrayList<>(), new ArrayList<>(), 0, 0, 0);

	private final List<Particle> particles;
	private final List<Border> walls;
	private final double wallAbsorbedMomentum;
	private final double targetTime;
	private final double currentTime;

	private SimState(
			List<Particle> particles,
			List<Border> walls,
			double wallAbsorbedMomentum,
			double targetTime,
			double currentTime) {

		this.particles = particles;
		this.walls = walls;
		this.wallAbsorbedMomentum = wallAbsorbedMomentum;
		this.targetTime = targetTime;
		this.currentTime = currentTime;
	}

	public static SimState createWithWalls(double width, double height) {
		return NULL
			.addWall(Border.b(0, Border.BY_X))
			.addWall(Border.b(width, Border.BY_X))
			.addWall(Border.b(0, Border.BY_X))
			.addWall(Border.b(height, Border.BY_Y));
	}

	public SimState addWall(Border b) {
		return new SimState(
			particles,
			add(walls, b),
			wallAbsorbedMomentum,
			targetTime,
			currentTime);
	}

	private <T> List<T> add(List<T> ts, T t) {
		ArrayList<T> newTs = new ArrayList<>(ts);
		newTs.add(t);
		return newTs;
	}

	public SimState addParticle(double posX, double posY, double velX, double velY) {
		return addParticle(Particle.p(Coord.c(posX, posY), Coord.c(velX, velY)));
	}

	public SimState addParticle(Particle p) {
		return new SimState(
			add(particles, p),
			walls,
			wallAbsorbedMomentum,
			targetTime,
			currentTime);
	}

	public Stream<Particle> particles() {
		return particles.stream();
	}

	public Stream<Border> walls() {
		return walls.stream();
	}

	public double wallAbsorbedMomentum() {
		return wallAbsorbedMomentum;
	}

	public double targetTime() {
		return targetTime;
	}

	public double currentTime() {
		return currentTime;
	}

	public SimState currentTime(double ct) {
		return new SimState(
			particles,
			walls,
			wallAbsorbedMomentum,
			targetTime,
			ct);
	}

	public SimState targetTime(double tt) {
		return new SimState(
			particles,
			walls,
			wallAbsorbedMomentum,
			tt,
			currentTime);
	}

	public SimState wallAbsorbedMomentum(double am) {
		return new SimState(
			particles,
			walls,
			am,
			targetTime,
			currentTime);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(currentTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((particles == null) ? 0 : particles.hashCode());
		temp = Double.doubleToLongBits(targetTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(wallAbsorbedMomentum);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((walls == null) ? 0 : walls.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimState other = (SimState) obj;
		if (Double.doubleToLongBits(currentTime) != Double.doubleToLongBits(other.currentTime))
			return false;
		if (particles == null) {
			if (other.particles != null)
				return false;
		} else if (!particles.equals(other.particles))
			return false;
		if (Double.doubleToLongBits(targetTime) != Double.doubleToLongBits(other.targetTime))
			return false;
		if (Double.doubleToLongBits(wallAbsorbedMomentum) != Double.doubleToLongBits(other.wallAbsorbedMomentum))
			return false;
		if (walls == null) {
			if (other.walls != null)
				return false;
		} else if (!walls.equals(other.walls))
			return false;
		return true;
	}
}
