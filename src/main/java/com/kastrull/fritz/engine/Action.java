package com.kastrull.fritz.engine;

import java.util.function.Function;

public interface Action<T> extends Function<Double, T> {
	@Override
	public T apply(Double eventTime);
}