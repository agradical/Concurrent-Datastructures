package Queue;
import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String args[]) {
		int mode = Integer.parseInt(args[0]);
		int load = Integer.parseInt(args[1]);
		int nThreads = Integer.parseInt(args[2]);
		
		//WARNING: If testcorrectness is set reduce the number of operations in OperationExecutor
		//as testing correctness is very time consuming
		int testcorrectness = 0;
		
		if(args.length > 3) {
			testcorrectness = Integer.parseInt(args[3]);
		}	
		
		Operation operation;
		
		if(mode == 0 ) {
			operation = new LockBased(); 
		} else {
			operation = new LockFree(); 
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
		
		ll.print(mode);
		
		if(testcorrectness != 0) {
			boolean consistent = ll.isConsistent(mode);
			boolean correct = true;

			OperationExecutor.combileAndSortAllThreadLogs();
			ArrayList<OperationLogger> opsList = OperationExecutor.operationList;
			ArrayList<OperationLogger> enqOpsList = new ArrayList<OperationLogger>();
			ArrayList<OperationLogger> deqOpsList = new ArrayList<OperationLogger>();
			for(OperationLogger log: opsList) {
				if(log.operationType == OperationType.ENQUEUE) {
					enqOpsList.add(log);
				} else if(log.operationType == OperationType.DEQUEUE) {
					deqOpsList.add(log);
				}
			}

			while(enqOpsList.size() > 0) {
				OperationLogger enqlog = enqOpsList.get(0);
				List<OperationLogger> overlapenqloglist = new ArrayList<OperationLogger>();
				OperationLogger enqOperationtoBeRemoved = null;
				boolean validdeqops = false;
				boolean keyfound = false;
				for(int i=0; i<enqOpsList.size(); i++) {
					OperationLogger overlapenqlog = enqOpsList.get(i);
					if(enqlog.endtime >= overlapenqlog.starttime) {
						overlapenqloglist.add(overlapenqlog);
					}
				}

				List<OperationLogger>  dqOperationstoBeRemoved = new ArrayList<OperationLogger>();
				while(deqOpsList.size() > 0) {
					OperationLogger deqlog = deqOpsList.get(0);
					if(deqlog.key < 0 || (deqlog.endtime < enqlog.starttime)) {
						dqOperationstoBeRemoved.add(deqlog);
					} else {
						validdeqops = true;
						deqOpsList.removeAll(dqOperationstoBeRemoved);
						dqOperationstoBeRemoved = new ArrayList<OperationLogger>();
						List<OperationLogger> overlappingdeqopslist = new ArrayList<OperationLogger>();
						for(OperationLogger overlappingdeqops: deqOpsList) {
							if(deqlog.endtime >= overlappingdeqops.starttime) {
								overlappingdeqopslist.add(overlappingdeqops);
							}
						}
						for(OperationLogger _enqlog: overlapenqloglist) {
							for(OperationLogger _dqlog : overlappingdeqopslist) {
								if(_enqlog.key == _dqlog.key) {
									keyfound = true;
									enqOperationtoBeRemoved = _enqlog;
									dqOperationstoBeRemoved.add(_dqlog);
									break;
								}
							}
							deqOpsList.removeAll(dqOperationstoBeRemoved);
							if(keyfound) {
								break;
							}
						}
						break;
					}
					deqOpsList.removeAll(dqOperationstoBeRemoved);
				}

				enqOpsList.remove(enqOperationtoBeRemoved);
				//No more deq operations left
				if(enqOperationtoBeRemoved == null) {
					break;
				}
				if(!keyfound && validdeqops) {
					correct = false;
					break;
				}

				if(!validdeqops) {
					break;
				}
			}


			if(correct&&consistent) {
				System.out.println("LL is correct and consistent");
			} else if(!correct){
				System.out.println("LL is consistent but NOT correct");
			} else {
				System.out.println("LL is NEITHER consistent NOR correct");
			}
		}
	}
}
