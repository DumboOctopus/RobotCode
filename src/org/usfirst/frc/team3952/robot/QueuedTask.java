package org.usfirst.frc.team3952.robot;

import java.util.*;

public class QueuedTask extends Task {
	private Queue<Task> queue;
	
	public QueuedTask(Task... tasks) {
		queue = new LinkedList<>();
		for(Task task : tasks) {
			queue.offer(task);
		}
	}
	
	@Override
	public boolean run() {
		if(queue.peek().run()) {
			queue.poll();
		}
		return queue.size() == 0;
	}

	@Override
	public void cancel() {
		if(queue.peek() != null) {
			queue.peek().cancel();
		}
	}

	@Override
	public String toString() {
		return "Queued Task(Currently running: " + queue.peek() == null ? "null" : queue.peek().toString() + ")";
	}

}
