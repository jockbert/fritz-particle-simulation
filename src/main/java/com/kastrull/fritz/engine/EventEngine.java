package com.kastrull.fritz.engine;

import java.util.Iterator;

public interface EventEngine<R> extends Iterator<Outcome<R>> {

	static <S> EventEngine<S> create() {
		return new BasicEventEngine<S>();
	}

	void addEvent(double time, Action<R> eventAction, int... involving);

	void addEvent(Event<R> event);

}
