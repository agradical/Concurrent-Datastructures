package Algorithms.algorithm;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import Algorithms.task.Counter;
import Algorithms.task.Task;

public class PetersonTournament implements Algorithm {
	
	Task task = new Counter(0);
	Map<String, Peterson> threadLockMap = new ConcurrentHashMap<String, Peterson>();
	AtomicInteger id = new AtomicInteger(0);
	ThreadLocal<Integer> threadID = new ThreadLocal<Integer>();
	int[] threadLevel;
	int maxLevel = 0;
	
	public PetersonTournament(Integer nThreads) {
		
		int i = (int)Math.ceil((Math.log(nThreads)/Math.log(2)));
		threadLevel = new int[nThreads];
		
		int level = 0;
		
		do {
			maxLevel = level;
			for(int j=0; j<nThreads; j++) {
				//System.out.println(level+"----"+((int)Math.pow(2, level+1)));
				if(j%((int)Math.pow(2, level+1)) == 0) {
					Peterson algo = new Peterson();
					threadLockMap.put(j+"-"+level, algo);
					//System.out.println("algo instance: "+ algo.hashCode());
				} else {
					Peterson algo = threadLockMap.get((j-1)+"-"+level);
					threadLockMap.put(j+"-"+level, algo);
					//System.out.println("algo instance: "+ algo.hashCode());
				}
			}
			level++;
			i--;
		} while(i > 0);
		
	}
	
	@Override
	public void run() {
		//instantiate peterson lock when there's two thread
		threadID.set(id.getAndIncrement());
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
		int id = threadID.get();
		while(threadLevel[id] <= maxLevel) {
			//System.out.println("debug: lock "+id+"-"+threadLevel[id]);
			Peterson algo = threadLockMap.get(id+"-"+threadLevel[id]);
			algo.getIDToken();
			algo.lock();
			threadLevel[id]++;
		}
	}

	@Override
	public void unlock() {
		int id = threadID.get();
		while(threadLevel[id] > 0) {
			threadLevel[id]--;
			//System.out.println("debug: unlock "+id+"-"+threadLevel[id]);
			Peterson algo = threadLockMap.get(id+"-"+threadLevel[id]);
			algo.unlock();
			algo.returnIdToken();
		}
	}

	@Override
	public void output() {
		task.output();
	}
}
