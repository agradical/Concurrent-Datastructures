package Algorithms.algorithm;

import java.util.concurrent.atomic.AtomicBoolean;

import Algorithms.task.Counter;
import Algorithms.task.Task;

public class TestAndSet implements Algorithm {
	
	Task task = new Counter(0);
	volatile AtomicBoolean lock = new AtomicBoolean(false);
	
	@Override
	public void run() {
		int i=0;
		while(i<Task.iterations) {
			lock();
			task.execute();
			unlock();
			i++;
		}
	}

	@Override
	public void lock() {
		while(lock.getAndSet(true)) {};
	}

	@Override
	public void unlock() {
		lock.set(false);
	}

	@Override
	public void output() {
		task.output();		
	}

}
