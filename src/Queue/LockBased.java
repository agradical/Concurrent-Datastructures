package Queue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockBased implements Operation {
	
	volatile Lock enquelock = new ReentrantLock(true);
	volatile Lock dequelock = new ReentrantLock(true);
	public LinkedList ll;

	public LockBased() {
		this.ll = LinkedList.getInstance();
	}
	
	@Override
	public boolean isEmpty() {
		return ll.HEAD.next == null;
	}

	@Override
	public void enqueue(int val) {
		enquelock.lock();
		Node n = new Node(val);
		ll.TAIL.next = n;
		ll.TAIL = n;
		enquelock.unlock();
	}

	@Override
	public int dequeue() {
		int val = -1;
		dequelock.lock();
		if(ll.HEAD.next == null) {
			//return ERROR_CODE
			val = -2;
		} else {
			val = ll.HEAD.next.key;
			ll.HEAD = ll.HEAD.next;
		}
		dequelock.unlock();
		return val;
	}
	
}
