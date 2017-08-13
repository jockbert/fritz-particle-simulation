package com.kastrull.fritz;

import static com.codepoetics.protonpack.StreamUtils.zip;
import static com.kastrull.fritz.primitives.Coord.c;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.kastrull.fritz.primitives.Coord;
import com.kastrull.fritz.sim.DummySimulator;
import com.kastrull.fritz.sim.SimSetup;
import com.kastrull.fritz.sim.SimState;
import com.kastrull.fritz.sim.SimStateGenerator;
import com.kastrull.fritz.sim.Simulator;

public class TestIdealGasLaw {

	private static final double DOUBLE_TEMP_FACTOR = Math.sqrt(2.0);

	public static void main(String[] args) {

		double sizeX = 1_000;
		double sizeY = 1_000;
		int particleCount = 10_000;
		int maxSpeed = 10_000;
		int simTime = 1;
		long rndSeed = 0;

		SimSetup norm1 = SimSetup.ss(
			c(sizeX, sizeY), particleCount,
			maxSpeed, simTime,
			rndSeed, "Norm 1");

		SimSetup norm2 = SimSetup.ss(
			c(sizeX, sizeY), particleCount,
			maxSpeed, simTime,
			rndSeed + 1, "Norm 2");

		SimSetup norm3 = SimSetup.ss(
			c(sizeX, sizeY), particleCount,
			maxSpeed, simTime,
			rndSeed + 2, "Norm 3");

		SimSetup doublePC = SimSetup.ss(
			c(sizeX, sizeY), particleCount * 2,
			maxSpeed, simTime,
			rndSeed, "Double particle count");

		SimSetup halfVolume1 = SimSetup.ss(
			c(sizeX, sizeY / 2), particleCount,
			maxSpeed, simTime,
			rndSeed, "Half volume 1");

		SimSetup halfVolume2 = SimSetup.ss(
			c(sizeX / 2, sizeY), particleCount,
			maxSpeed, simTime,
			rndSeed, "Half volume 2");

		SimSetup doubleTemp = SimSetup.ss(
			c(sizeX / 2, sizeY), particleCount,
			maxSpeed * DOUBLE_TEMP_FACTOR, simTime,
			rndSeed, "Double temperature");

		SimSetup quadruplePC = SimSetup.ss(
			c(sizeX, sizeY), particleCount * 2,
			maxSpeed, simTime,
			rndSeed, "Quadruple particle count");

		SimSetup quarterVolume = SimSetup.ss(
			c(sizeX / 2, sizeY / 2), particleCount,
			maxSpeed, simTime,
			rndSeed, "Quarter volume");

		SimSetup quadrupleTemp = SimSetup.ss(
			c(sizeX / 2, sizeY), particleCount,
			maxSpeed * DOUBLE_TEMP_FACTOR * DOUBLE_TEMP_FACTOR, simTime,
			rndSeed, "Quadruple temperature");

		List<SimSetup> setups = Arrays.asList(
			norm1, norm2, norm3, doublePC,
			halfVolume1, halfVolume2, doubleTemp, quadruplePC,
			quarterVolume, quadrupleTemp);

		Simulator simulator = new DummySimulator();

		Stream<SimState> endStates = setups.stream()
			.map(SimStateGenerator::generate)
			.map(simulator::simulate);

		Stream<ResultPair> results = zip(
			setups.stream(),
			endStates,
			ResultPair::new);

		printRow("PRESSURE", "NAME");
		printHorisontalLine();
		results.forEach(TestIdealGasLaw::printResult);
		printHorisontalLine();
	}

	private static void printHorisontalLine() {
		char[] arr = new char[70];
		Arrays.fill(arr, '-');
		System.out.println(// NOSONAR
			new String(arr) + System.lineSeparator());
	}

	private static void printResult(ResultPair pair) {
		Coord size = pair.setup.size;
		double circumference = (size.x + size.y) * 2.0;
		double time = pair.state.currentTime();
		double momentum = pair.state.wallAbsorbedMomentum();

		double pressure = momentum / circumference / time;

		printRow(pressure, pair.setup.name);
	}

	private static void printRow(Object pressure, String name) {
		String column1 = alignRight(pressure, 16);
		String column2 = name;
		System.out.println(column1 + "   " + column2); // NOSONAR
	}

	private static String alignRight(Object content, int width) {
		String result = content.toString();
		while (result.length() < width)
			result = ' ' + result; // NOSONAR
		return result;
	}

	private static class ResultPair {
		public final SimSetup setup;
		public final SimState state;

		ResultPair(SimSetup a, SimState b) {
			this.setup = a;
			this.state = b;
		}
	}
}
