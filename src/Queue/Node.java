package Queue;
import java.util.concurrent.atomic.AtomicReference;

public class Node {
	public int key;
	public Node next;
	public AtomicReference<Node> atomicNext;

	public Node(int key) {
		this.key = key;
		this.next = null;
		this.atomicNext = new AtomicReference<Node>(this.next);
	}
}
