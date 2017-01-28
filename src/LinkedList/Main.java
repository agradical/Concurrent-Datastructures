package LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

public class Main {
	public static void main(String args[]) {
		int mode = Integer.parseInt(args[0]);
		int load = Integer.parseInt(args[1]);
		int nThreads = Integer.parseInt(args[2]);
		
		Operation operation;
		
		if(mode == 1 ) {
			operation = new FineGrainLock(); 
		} else if(mode == 2 ) {
			operation = new LockFree(); 
		} else {
			operation = new CoarseGrainLock();
		}
		
		Long starttime = System.currentTimeMillis();
		OperationExecutor executor = new OperationExecutor(operation, load);
		List<Thread> threads = new ArrayList<Thread>();
		for(int i=0; i<nThreads; i++) {
			Thread t = new Thread(executor);
			t.setName("Thread-"+i);
			threads.add(t);
			t.start();
		}
		
		boolean isAnyThreadRunning = true;
		while(isAnyThreadRunning) {
			isAnyThreadRunning = false;
			for (Thread thread : threads) {
				if (thread.isAlive()) {
					isAnyThreadRunning = true;
					break;
				}
			}
		}
		Long endtime = System.currentTimeMillis();
		
		System.out.println("Time taken: "+ (endtime-starttime)+"ms");
		
		LinkedList ll = LinkedList.getInstance();
		if(mode != 2) {
			//ll.print();
		} else {
			//ll.printref();
		}
		
		boolean consistent = ll.isConsistent(mode);
		boolean correct = true;
		
		OperationExecutor.convert();
		
		for(Entry<Integer, ArrayList<OperationLogger>> entry: OperationExecutor.operationMap.entrySet()) {
			int key = entry.getKey();
			
			boolean result = ll.find(key, mode);
			//System.out.println(key);
			List<OperationLogger> possibleTypes = OperationExecutor.getLastWriteOperation(key);
			if(possibleTypes.size() == 0) {
				continue;
			}
			boolean insert = false;
			boolean delete = false;
			for(OperationLogger log: possibleTypes) {
				OperationType type = log.operationType;
				if(type == OperationType.INSERT) {
					insert = true;
				}
				if(type == OperationType.DELETE) {
					delete = true;
				}
			}
			if(insert && delete) {
				continue;
			} else {
				if(insert) {
					if(!result) {
						correct = false;
						List<OperationLogger> logger =  OperationExecutor.operationMap.get(key);
						for(OperationLogger log: logger) {
							System.out.println(log.toString());
						}
						
						break;
					}
 				} if(delete) {
 					if(result) {
 						correct = false;
 						List<OperationLogger> logger =  OperationExecutor.operationMap.get(key);
 						for(OperationLogger log: logger) {
							System.out.println(log.toString());
						}
 						
 						break;
 					}
 				}
			}
		}
		
		if(correct&&consistent) {
			System.out.println("LL is correct and consistent");
		} else if(!correct){
			System.out.println("LL is NOT correct");
		}
		
	}
}
