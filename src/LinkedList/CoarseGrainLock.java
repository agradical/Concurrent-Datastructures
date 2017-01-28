package LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;

public class CoarseGrainLock implements Operation  {

	volatile AtomicBoolean lock = new AtomicBoolean(false);
	
	public LinkedList ll;
	
	public CoarseGrainLock() {
		this.ll = LinkedList.getInstance();
	}
	
	@Override
	public boolean contains(int val) {
		Node head = ll.getHead();
		Node curr = head;
		while(curr.key <= val) {
			if(curr.key == val) {
				return true;
			}
			curr = curr.next;
		}
		return false;
	}

	@Override
	public boolean insert(int val) {
		lock();
		
		Node head = ll.getHead();
		Node pred = head;
		Node curr = pred.next;
		while(curr.key < val) {
			pred = curr;
			curr = curr.next;
		}
		if(curr.key == val) {
			unlock();
			return false;
		}
		Node node = new Node(val);
		node.next = curr;
		pred.next = node;
		
		unlock();
		return true;
	}

	@Override
	public boolean remove(int val) {
		lock();
		Node head = ll.getHead();
		Node pred = head;
		Node curr = pred.next;
		while(curr.key < val) {
			pred = curr;
			curr = curr.next;
		}
		if(curr.key != val) {
			unlock();
			return false;
		}
		pred.next = curr.next;
		unlock();
		return true;
	}
	
	public void lock() {
		while(lock.getAndSet(true)) {};
	}

	public void unlock() {
		lock.set(false);
	}
	
}
