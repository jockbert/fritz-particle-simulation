package com.kastrull.fritz.physics;

import java.util.Arrays;
import java.util.stream.Stream;

public interface PolySolver {

	Stream<Double> findRealRoots(SecondDegPolynomial poly);

	default Stream<Double> streamOf(Double... roots) {
		return Arrays.asList(roots).stream();
	}

	/** Polynomial to solve: <code>0 = a * t^2 + b * t + c </code> */
	static Stream<Double> findRealRoots(double a, double b, double c) {
		SecondDegPolynomial poly = SecondDegPolynomial.create(a, b, c);

		return new BasicSolver().findRealRoots(poly);
	}

	final class BasicSolver implements PolySolver {

		/**
		 * Polynomial to solve: <code>0 = a * t^2 + b * t + c </code> <br>
		 * Found roots are Real numbers on the form:
		 * <code>t = -b/a +- sqrt((b/a)^2-c/a)</code>
		 *
		 * @param poly
		 *            The polynomial to solve
		 * @return
		 */
		@Override
		public Stream<Double> findRealRoots(SecondDegPolynomial poly) {
			if (poly.a == 0)
				// has problem with division by zero
				return streamOf();

			double partInSqrt = (poly.b / poly.a * poly.b - poly.c) / poly.a;
			double term1 = -poly.b / poly.a;

			boolean partInSqrtIsNegative = partInSqrt < 0
					|| (poly.a == Double.POSITIVE_INFINITY && poly.c > 0);

			if (partInSqrtIsNegative)
				// no real solution exists
				return streamOf();
			if (partInSqrt == 0)
				// only one solution exists
				return streamOf(term1);
			if (poly.c == 0)
				// avoid numerical problem x-Math.sqrt(x*x) != 0
				return streamOf(0.0, term1 * 2);

			double term2 = Math.sqrt(partInSqrt);
			return streamOf(term1 - term2, term1 + term2);
		}
	}

	/**
	 * Polynomial on the form p(t) = a * t^2 + b * t + c
	 */
	final class SecondDegPolynomial {
		private final double a;
		private final double b;
		private final double c;

		SecondDegPolynomial(double a, double b, double c) {
			this.a = a;
			this.b = b;
			this.c = c;
		}

		static SecondDegPolynomial create(double a, double b, double c) {
			return new SecondDegPolynomial(a, b, c);
		}
	}
}