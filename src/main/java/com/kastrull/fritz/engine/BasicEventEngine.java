package com.kastrull.fritz.engine;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BasicEventEngine<I, R> implements EventEngine<I, R> {

	private static Comparator<Event<?, ?>> ORDER_BY_TIME = (o1, o2) -> Double
		.valueOf(o1.time())
		.compareTo(o2.time());

	Queue<Event<I, R>> queue = new PriorityQueue<>(ORDER_BY_TIME);

	@Override
	public boolean hasNext() {
		return !queue.isEmpty();
	}

	@Override
	public Outcome<I, R> next() {
		Event<I, R> event = queue.poll();

		double time = event.time();
		R result = event.result();
		Set<I> involving = event.involving();
		Set<I> invalidated = recursivelyFindInvalidated(involving);

		return Outcome
			.<I, R>of(time, result)
			.involves(involving)
			.invalidates(invalidated);
	}

	private Set<I> recursivelyFindInvalidated(Set<I> involving) {
		if (involving.isEmpty())
			// recursion end criteria fulfilled
			return Collections.emptySet();

		List<Event<I, R>> invalidatedEvents = queue.stream()
			.filter(isInvalidatedBy(involving))
			.collect(Collectors.toList());

		Set<I> invalidatedItems = invalidatedEvents.stream()
			.flatMap(i -> i.involving().stream())
			.collect(Collectors.toSet());

		queue.removeAll(invalidatedEvents);

		invalidatedItems.removeAll(involving);

		// invalidatedItems are the new involving, in next recursion step.
		invalidatedItems.addAll(recursivelyFindInvalidated(invalidatedItems));

		return invalidatedItems;
	}

	public static <J, S> Predicate<Event<J, S>> isInvalidatedBy(Set<J> invalidators) {
		return alpha -> invalidators.stream()
			.anyMatch(i -> alpha.involving().contains(i));
	}

	@Override
	public void addEvent(Event<I, R> event) {
		queue.add(event);
	}
}
