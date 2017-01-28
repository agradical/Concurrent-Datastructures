package Queue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OperationExecutor implements Runnable {

	public Operation operation;
	public static ArrayList<OperationLogger> operationList = new ArrayList<OperationLogger>();
	public static ConcurrentHashMap<String, ArrayList<OperationLogger>> operationThreadMap 
									= new ConcurrentHashMap<String, ArrayList<OperationLogger>>();
	
	int load;
	public volatile AtomicInteger maxOperations = new AtomicInteger(1000000);
	static int NUMBERRANGE = 100;
	
	public OperationExecutor(Operation operation, int load) {
		this.operation = operation;
		this.load = load;
	}
	
	@Override
	public void run() {
		
		while(maxOperations.getAndDecrement() > 0) {
			double randomops = Math.ceil(Math.random()*100);
			double randomnumber = Math.ceil(Math.random()*NUMBERRANGE);
			OperationType type = null;
			Long starttime = System.currentTimeMillis();
			if(load == 0) {
				if(randomops < 50) {
					type = OperationType.ENQUEUE;
					operation.enqueue((int)randomnumber);
				} else {
					type = OperationType.DEQUEUE;
					randomnumber = operation.dequeue();
				}
			} else if (load == 1) {
				if(randomops < 40) {
					type = OperationType.ENQUEUE;
					operation.enqueue((int)randomnumber);
				} else if (randomops < 80) {
					type = OperationType.DEQUEUE;
					randomnumber = operation.dequeue();
				} else {
					type = OperationType.ISEMPTY;
					operation.isEmpty();
				}
			} 
			Long endtime = System.currentTimeMillis();
//			System.out.println(Thread.currentThread().getName()
//					+":"+type.toString()+":"+(int)randomnumber
//					+":"+starttime+":"+endtime);
//			
			OperationLogger log = new OperationLogger();
			log.operationType = type;
			log.starttime = starttime;
			log.endtime = endtime;
			log.key = (int)randomnumber;
			log.threadName = Thread.currentThread().getName();

			if(operationThreadMap.containsKey(Thread.currentThread().getName())) {
				ArrayList<OperationLogger> array = operationThreadMap.get(Thread.currentThread().getName());
				if(array == null) {
					array = new ArrayList<OperationLogger>();
				}
				array.add(log);
				
			} else {
				ArrayList<OperationLogger> array = new ArrayList<OperationLogger>();
				array.add(log);
				operationThreadMap.put(Thread.currentThread().getName(), array);
			}
		}	
	}
	
	public static void combileAndSortAllThreadLogs() {
		for(Entry<String, ArrayList<OperationLogger>> entry: operationThreadMap.entrySet()) {
			operationList.addAll(entry.getValue());
		}
		Collections.sort(operationList);
	}
}
