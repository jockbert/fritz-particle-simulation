package com.kastrull.fritz.engine;

import java.util.function.Function;

/** Type alias for a function from time (as double) to some result of type T. */
public interface Action<T> extends Function<Double, T> {
	@Override
	public T apply(Double eventTime);
}