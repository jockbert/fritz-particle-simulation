package com.kastrull.fritz.physics;

import java.util.Optional;

import com.kastrull.fritz.primitives.Border;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Interaction;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.primitives.WallInteraction;

public interface Physics {

	Optional<Double> collisionTime(Particle a, Particle b);

	Coord posAtTime(Particle p, double time);

	Interaction interact(Particle before1, Particle before2);

	WallInteraction interactWall(Particle p, Border wall);

}
