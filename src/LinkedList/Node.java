package LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class Node {
	public int key;
	public Node next;
	public boolean mark;
	volatile AtomicBoolean lock = new AtomicBoolean(false);
	public AtomicMarkableReference<Node> atomicNext;
	
	public Node(int key) {
		this.key = key;
		this.next = null;
		this.atomicNext = new AtomicMarkableReference<Node>(this.next, false);
	}
	
	public void setNext(Node next) {
		this.next = next;
	}
	
	public Node(Node that) {
		this.key = that.key;
		this.next = that.next;
		this.atomicNext = that.atomicNext;
	}
	
	public boolean checklock() {
		return this.lock.get();
	}
	
	public void lock() {
		while(lock.getAndSet(true)) {};
	}

	public void unlock() {
		lock.set(false);
	}
}
