package LinkedList;

public class OperationLogger implements Comparable<OperationLogger>{
	OperationType operationType;
	String threadName;
	int key;
	long starttime;
	long endtime;
	
	@Override
	public int compareTo(OperationLogger o) {
		if (o.endtime - this.endtime > 0)
			return 1;
		else if(o.endtime == this.endtime)
			return 0;
		else
			return -1;
	}
	
	@Override
	public String toString() {
		return threadName+":"+operationType+":"+key+":"+starttime+":"+endtime;
	}
}
