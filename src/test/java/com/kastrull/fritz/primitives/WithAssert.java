package com.kastrull.fritz.primitives;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import com.kastrull.fritz.Laws;

public interface WithAssert {

	default void assertApprox(double expected, double actual) {
		assertApprox("", expected, actual);
	}

	default void assertApprox(String msg, double expected, double actual) {
		double delta = expected * Laws.EPSILON;
		assertEquals(
			messagePrefix(msg) + expected + " = " + actual + " ± " + delta,
			expected, actual, delta);
	}

	default void assertExact(double expected, double actual) {
		assertEquals(expected, actual, 0.0);
	}

	default void assertExact(String msg, double expected, double actual) {
		assertEquals(msg, expected, actual, 0.0);
	}

	default <T extends Approx<T>> void assertApprox(T c1, T c2) {
		assertApprox("", c1, c2);
	}

	default <T extends Approx<T>> void assertApprox(String msg, T c1, T c2) {
		String prefix = (!msg.isEmpty()) ? msg + ", " : "";
		assertTrue(
			prefix + c1 + " ~= " + c2,
			c1.approxEq(c2));
	}

	default <T extends Approx<T>> void assertNotApprox(T c1, T c2) {
		assertNotApprox("", c1, c2);
	}

	default <T extends Approx<T>> void assertNotApprox(String msg, T c1, T c2) {
		assertFalse(
			messagePrefix(msg) + c1 + " !~= " + c2,
			c1.approxEq(c2));
	}

	default String messagePrefix(String msg) {
		return (!msg.isEmpty()) ? msg + ", " : "";
	}

	default void assertApprox(String msg, double expected, Optional<Double> actual) {
		String prefix = messagePrefix(msg);
		assertNotNull(
			prefix + "is not null",
			actual);
		assertTrue(
			prefix + "is present, but is " + actual,
			actual.isPresent());
		assertApprox(msg, expected, actual.get());
	}

	default void assertNone(String msg, Optional<Double> actual) {
		String prefix = messagePrefix(msg);
		assertNotNull(prefix + "is not null", actual);
		assertFalse(prefix + " is not present, but is " + actual,
			actual.isPresent());
	}
}
