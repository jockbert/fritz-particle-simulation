package com.kastrull.fritz.engine;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BasicEventEngine<T> implements EventEngine<T> {

	private static Comparator<Event<?>> ORDER_BY_TIME = (o1, o2) -> Double.valueOf(o1.time())
		.compareTo(o2.time());

	Queue<Event<T>> queue = new PriorityQueue<>(ORDER_BY_TIME);

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Outcome<T> next() {
		Event<T> event = queue.poll();

		List<Event<T>> invalidatedEvents = queue.stream()
			.filter(isInvalidatedBy(event))
			.collect(Collectors.toList());

		queue.removeAll(invalidatedEvents);

		Set<Integer> invalidatedItems = invalidatedEvents.stream()
			.flatMap(i -> i.involving().stream())
			.collect(Collectors.toSet());

		invalidatedItems.removeAll(event.involving());

		double time = event.time();

		T result = event.action().apply(time);
		return Outcome
			.of(time, result)
			.involves(event.involving())
			.invalidates(invalidatedItems);
	}

	public static <S> Predicate<Event<S>> isInvalidatedBy(Event<S> invalidator) {
		return alpha -> invalidator.involving().stream()
			.anyMatch(i -> alpha.involving().contains(i));
	}

	@Override
	public void addEvent(double time, Action<T> action, int... involving) {
		queue.add(
			Event.of(time, action).involving(involving));
	}

	@Override
	public void addEvent(Event<T> event) {
		queue.add(event);
	}
}
