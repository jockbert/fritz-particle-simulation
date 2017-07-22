package com.kastrull.fritz.physics;

import java.util.Optional;

import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;

public interface Physics {

	Optional<Double> collisionTime(Particle a, Particle b);

	Coord posAtTime(Particle p, double time);

}
