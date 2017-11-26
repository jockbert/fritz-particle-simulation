package com.kastrull.fritz;

import java.io.File;
import java.util.stream.Collectors;

import com.kastrull.fritz.physics.LinearPhysics;
import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.primitives.Particle;
import com.kastrull.fritz.sim.BasicSimulator;
import com.kastrull.fritz.sim.SimState;
import com.kastrull.particle_sim_config.Config;
import com.kastrull.particle_sim_config.ConfigReader;

public class Main {

	private static final ConfigReader READER = ConfigReader.create();
	private static final BasicSimulator SIM = new BasicSimulator(new LinearPhysics());
	private static SimState state;

	public static void main(String[] args) throws Exception {

		println("Fritz particle simulator");
		println("========================");
		println("");

		int argCount = args.length;
		if (argCount != 1) {
			printHelp("Invalid number of arguments.\n"
					+ "Expecting a configuration input file.");
			System.exit(1);
		}

		File inFile = new File(args[0]);

		if (!inFile.exists()) {
			printHelp("Nonexisting input file " + inFile.getCanonicalPath().toString());
			System.exit(1);
		} else if (!inFile.canRead()) {
			printHelp("Unable to read file " + inFile.getCanonicalPath().toString());
			System.exit(1);
		}

		Config config = READER.read(inFile);

		state = conf2fritz(config);

		config.results.forEach(configResult -> {

			state = state.targetTime(configResult.time);
			println("");
			println("Simulating time from " + state.currentTime() + " to " + state.targetTime() + " ...");
			println("");

			state = SIM.simulate(state);

			println("Time ................: " + configResult.time);
			println("Expected momentum ...: " + configResult.momentum);
			println("Simulated momentum ..: " + state.wallAbsorbedMomentum());
		});

		println("simulation done!");
	}

	private static SimState conf2fritz(Config config) {
		SimState state = SimState
			.createWithWalls(config.area.x, config.area.y);

		state = state.particles(
			config.particles.stream()
				.map(Main::conf2fritz)
				.collect(Collectors.toList()));
		return state;
	}

	private static Particle conf2fritz(Config.Particle p) {
		return Particle.p(
			conf2fritz(p.position),
			conf2fritz(p.velocity));
	}

	private static Coord conf2fritz(Config.Vector v) {
		return Coord.c(v.x, v.y);
	}

	private static void printHelp(String message) {
		println(message);
		println("");
		println("Expected arguments to program:");
		println("  <input-file>");
		println("");
	}

	private static void println(String line) {
		System.out.println(line); // NOSONAR
	}
}
