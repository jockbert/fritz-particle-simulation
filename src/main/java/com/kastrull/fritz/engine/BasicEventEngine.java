package com.kastrull.fritz.engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BasicEventEngine<T> implements EventEngine<T> {

	private static Comparator<Event<?>> ORDER_BY_TIME = (o1, o2) -> Double
		.valueOf(o1.time())
		.compareTo(o2.time());

	Queue<Event<T>> queue = new PriorityQueue<>(ORDER_BY_TIME);

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Outcome<T> next() {
		Event<T> event = queue.poll();

		Set<Integer> involving = event.involving();
		Set<Integer> invalidated = recursivelyFindInvalidated(involving);

		double time = event.time();
		T result = event.result();

		return Outcome
			.of(time, result)
			.involves(involving)
			.invalidates(invalidated);
	}

	private Set<Integer> recursivelyFindInvalidated(Set<Integer> involving) {
		if (involving.isEmpty())
			// recursion end criteria fulfilled
			return Collections.emptySet();

		List<Event<T>> invalidatedEvents = queue.stream()
			.filter(isInvalidatedBy(involving))
			.collect(Collectors.toList());

		Set<Integer> invalidatedItems = invalidatedEvents.stream()
			.flatMap(i -> i.involving().stream())
			.collect(Collectors.toSet());

		queue.removeAll(invalidatedEvents);

		invalidatedItems.removeAll(involving);

		// invalidatedItems are the new involving, in next recursion step.
		invalidatedItems.addAll(recursivelyFindInvalidated(invalidatedItems));

		return invalidatedItems;
	}

	public static <S> Predicate<Event<S>> isInvalidatedBy(Set<Integer> invalidators) {
		return alpha -> invalidators.stream()
			.anyMatch(i -> alpha.involving().contains(i));
	}

	@Override
	public void addEvent(Event<T> event) {
		queue.add(event);
	}
}
