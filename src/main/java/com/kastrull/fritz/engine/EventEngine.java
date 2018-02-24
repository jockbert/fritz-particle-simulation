package com.kastrull.fritz.engine;

import java.util.Iterator;

/**
 * An event engine drives a series of events related to zero or more items.
 * Items are here represented by integer ID:s. Each event is executed in
 * ascending time-order. A future (planned) events can be invalidated if it
 * involves an item related to the currently executing event. An event is
 * executed when method {@link EventEngine#next()} is called. The invalidating
 * relation can be direct or indirect via other related events.
 *
 * <h2>An example</h2>
 * <p>
 * There exists 6 events:
 * <ul>
 * <li>E1 occurs at time 1 and involves item I1
 * <li>E2 occurs at time 2 and involves item I2
 * <li>E3 occurs at time 3 and involves item I3
 * <li>E4 occurs at time 4 and involves item I1
 * <li>E5 occurs at time 5 and involves items I1 and I3
 * <li>E6 occurs at time 6 and involves no items
 * <li>E7 occurs at time 7 and involves items 13 and I8
 * <li>E8 occurs at time 8 and involves items I8
 * </ul>
 * The only events NOT invalidated by executing E1 are E2 and E6, since these
 * events are not directly or indirectly related to item I1. Item I3 is related
 * to item I1 via event E5. For simplicity also event E3 is invalidated, despite
 * occurring before E5. I8 is indirectly related to I1 via events E5 and E7.
 */
public interface EventEngine<R> extends Iterator<Outcome<R>> {

	static <S> EventEngine<S> create() {
		return new BasicEventEngine<S>();
	}

	void addEvent(double time, Action<R> eventAction, int... involving);

	void addEvent(Event<R> event);
}
