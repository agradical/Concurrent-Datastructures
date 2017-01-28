package Algorithms.algorithm;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import Algorithms.task.Counter;
import Algorithms.task.Task;

public class Peterson implements Algorithm {
	
	volatile boolean flag[] = {false, false};
	volatile int victim = 0; 
	static Task task = new Counter(0);

	Map<Long, Integer> map = new ConcurrentHashMap<Long, Integer>();
	Queue<Integer> idToken = new LinkedBlockingQueue<Integer>();
	
	public Peterson() {
		idToken.add(0);
		idToken.add(1);
	}
	
	@Override
	public void run() {
		getIDToken();
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
		int i = map.get(Thread.currentThread().getId());
		int j =  1-i;
		flag[i] = true;
		victim = i;
		while (flag[j] && victim == i) {};
	}

	@Override
	public void unlock() {
		int i = map.get(Thread.currentThread().getId());
		flag[i] = false;
	}

	public void getIDToken() {
		int id = this.idToken.remove();
		//System.out.println(Thread.currentThread().getId() + "----" + id);
		map.put(Thread.currentThread().getId(), id);
	}
	
	public void returnIdToken() {
		int id = map.get(Thread.currentThread().getId());
		map.remove(Thread.currentThread().getId());
		this.idToken.add(id);
	}

	@Override
	public void output() {
		task.output();
	}
}
