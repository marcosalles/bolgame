package com.marcosalles.bolgame.event;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventActionQueue {
	private final Queue<Runnable> actionQueue = new ArrayBlockingQueue<>(500);
	private final Thread consumer;

	public EventActionQueue() {
		Logger.getLogger("EventActionQueue").log(Level.ALL, "Started queue");
		consumer = new Thread(() -> {
			while (true) {
				if (!actionQueue.isEmpty()) {
					actionQueue.poll().run();
				} else {
					try {
						Thread.sleep(300);
					} catch (InterruptedException e) {
					}
				}
			}
		});
		consumer.start();
	}

	public void queueAction(Runnable action) {
		actionQueue.add(action);
	}
}
