package LinkedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class OperationExecutor implements Runnable {

	public Operation operation;
	public static HashMap<Integer, ArrayList<OperationLogger>> operationMap 
									= new HashMap<Integer, ArrayList<OperationLogger>>();
	public static ConcurrentHashMap<String, HashMap<Integer, ArrayList<OperationLogger>>> operationThreadMap 
									= new ConcurrentHashMap<String, HashMap<Integer, ArrayList<OperationLogger>>>();
	
	int load;
	public volatile AtomicInteger maxOperations = new AtomicInteger(1000000);
	static int NUMBERRANGE = 1000;
	
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
				if(randomops < 90) {
					type = OperationType.SEARCH;
					operation.contains((int)randomnumber);
					//System.out.println("search:"+randomnumber);
				} else if (randomops < 99) {
					type = OperationType.INSERT;
					operation.insert((int)randomnumber);
					//System.out.println("insert:"+randomnumber);
				} else {
					type = OperationType.DELETE;
					operation.remove((int)randomnumber);
					//System.out.println("remove:"+randomnumber);
				}
			} else if (load == 1) {
				if(randomops < 70) {
					type = OperationType.SEARCH;
					operation.contains((int)randomnumber);
					//System.out.println("search:"+randomnumber);
				} else if (randomops < 90) {
					type = OperationType.INSERT;
					operation.insert((int)randomnumber);
					//System.out.println("insert:"+randomnumber);
				} else {
					type = OperationType.DELETE;
					operation.remove((int)randomnumber);
					//System.out.println("remove:"+randomnumber);
				}
			} else if(load == 2) {
				if(randomops < 50) {
					type = OperationType.INSERT;
					operation.insert((int)randomnumber);
					//System.out.println("insert:"+randomnumber);
				} else {
					type = OperationType.DELETE;
					operation.remove((int)randomnumber);
					//System.out.println("remove:"+randomnumber);
				}
			}
			Long endtime = System.currentTimeMillis();
//			System.out.println(Thread.currentThread().getName()
//					+":"+type.toString()+":"+(int)randomnumber
//					+":"+starttime+":"+endtime);
			
			OperationLogger log = new OperationLogger();
			log.operationType = type;
			log.starttime = starttime;
			log.endtime = endtime;
			log.key = (int)randomnumber;
			log.threadName = Thread.currentThread().getName();

			if(operationThreadMap.containsKey(Thread.currentThread().getName())) {
				HashMap<Integer, ArrayList<OperationLogger>> map = operationThreadMap.get(Thread.currentThread().getName());
				if(map.containsKey((int)randomnumber)) {
					ArrayList<OperationLogger> array = map.get((int)randomnumber);
					array.add(log);
				} else {
					ArrayList<OperationLogger> array = new ArrayList<OperationLogger>();
					array.add(log);
					map.put((int)randomnumber, array);
				}
			} else {
				ArrayList<OperationLogger> array = new ArrayList<OperationLogger>();
				array.add(log);
				HashMap<Integer, ArrayList<OperationLogger>> map = new HashMap<Integer, ArrayList<OperationLogger>>(); 
				map.put((int)randomnumber, array);
				operationThreadMap.put(Thread.currentThread().getName(), map);
			}
		}	
	}
	
	public static void convert() {
		for(Entry<String, HashMap<Integer, ArrayList<OperationLogger>>> entry: operationThreadMap.entrySet()) {
			for(Entry<Integer, ArrayList<OperationLogger>> ent: entry.getValue().entrySet()) {
				if(operationMap.containsKey(ent.getKey())) {
					operationMap.get(ent.getKey()).addAll(ent.getValue());
				}
				else {
					ArrayList<OperationLogger> array = new ArrayList<OperationLogger>();
					operationMap.put(ent.getKey(), array);
					operationMap.get(ent.getKey()).addAll(ent.getValue());
				}
				
			}
		}
	}
	
	public static List<OperationLogger> getLastWriteOperation(int key) {
		ArrayList<OperationLogger> logs = operationMap.get(key);
		
		List<OperationLogger> listTypes = new ArrayList<OperationLogger>();

		if(logs.size() == 0) {
			return listTypes;
		}
		
		Collections.sort(logs);
		
		long lastOpStartTime = 0;
		int count = 0;
		for(OperationLogger log: logs) {
			if(log.operationType == OperationType.SEARCH) {
				continue;
			}
			if(count == 0) {
				lastOpStartTime = log.starttime;
				listTypes.add(log);
			} else {
				if(log.endtime >= lastOpStartTime) {
					listTypes.add(log);
				}
			}
			count++;
		}
		return listTypes;
	}
}
