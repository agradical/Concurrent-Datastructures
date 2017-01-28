package Queue;
import java.util.concurrent.atomic.AtomicReference;

public class LinkedList {
	public Node HEAD, TAIL;
	public AtomicReference<Node> HEADREF, TAILREF;
	private static LinkedList ll;
	
	private LinkedList() {
		HEAD = new Node(0);
		TAIL = HEAD;
		HEADREF = new AtomicReference<Node>(HEAD);
		TAILREF = new AtomicReference<Node>(TAIL);
	}
	
	public static LinkedList getInstance() {
		if(ll == null) {
			ll = new LinkedList();
		}
		return ll;
	}
	
	public void print(int mode) {
		if(mode == 0) {
			Node temp = HEAD;
			System.out.print("HEAD"+"->");
			while(temp != TAIL && temp != null) {
				System.out.print(temp.key+"->");
				temp = temp.next;
			}
			System.out.println("TAIL"+"->X");
		} else {			
			AtomicReference<Node> temp = HEADREF;
			System.out.print("HEAD"+"->");
			while(temp != TAILREF && temp != null && temp.get() != null) {
				System.out.print(temp.get().key+"->");
				temp = temp.get().atomicNext;
			}
			System.out.println("TAIL"+"->X");
		}
	}
	
	public boolean isConsistent(int mode) {
		if(mode == 0) {
			Node head = HEAD;
			Node tail = TAIL;
			
			while(head != tail && head != null) {
				head = head.next;
			}
			if(head == null) {
				return false;
			} else {
				return true;
			}
			
		} else {
			AtomicReference<Node> head = HEADREF;
			AtomicReference<Node> tail = TAILREF;
			
			while(head != tail && head != null && head.get() != null) {
				head = head.get().atomicNext;
			}
			if(head == null || head.get() != null) {
				return false;
			} else {
				return true;
			}
		}
	}
	
}
