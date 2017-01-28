package LinkedList;
import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFree implements Operation {
	
	public LinkedList ll;
	
	public LockFree() {
		this.ll = LinkedList.getInstance();
	}
	
	@Override
	public boolean contains(int val) {
		Node node = find(val);
		if(node.atomicNext.getReference().key == val && !node.atomicNext.isMarked()) {
			return true;
		}
		return false;
	}

	public Node find(int val) {
		Node head = ll.getHead();
		Node pred = null;
		Node curr = null;
		Node succ = null;
		boolean[] marked = {false};
		//System.out.println(head+":"+pred+":"+pred.next+":"+pred.atomicNext.getReference()+":"+curr);
		retry: while(true) {
			pred = head;
			curr = pred.atomicNext.getReference();
			while(true) {
				succ = curr.atomicNext.get(marked);
				while(marked[0]) {
					boolean snip = pred.atomicNext.compareAndSet(curr, succ, false, false);
					if (!snip) {
						continue retry;
					}
					curr = succ;
					succ = curr.atomicNext.get(marked);
				}
				if(curr.key >= val) {
					return pred;
				}
				pred = curr;
				curr = succ;				
			}
		}
	}
	
	@Override
	public boolean insert(int val) {
		
		while(true) {
			Node pred = find(val);
			Node curr = pred.atomicNext.getReference();
			if(curr.key == val) {
				return true;
			}
			Node node = new Node(val);
			node.atomicNext = new AtomicMarkableReference<Node>(curr, false);
			boolean result = pred.atomicNext.compareAndSet(curr, node, false, false);
			if(result) {
				return true;
			}
		}
	}

	@Override
	public boolean remove(int val) {
		boolean snip;
		while(true) {
			Node pred = find(val);
			Node curr = pred.atomicNext.getReference();
			if(curr.key != val) {
				return false;
			}
			Node succ = curr.atomicNext.getReference();
			snip = curr.atomicNext.attemptMark(succ, true);
			if(!snip) {
				continue;
			}
			pred.atomicNext.compareAndSet(curr, succ, false, false);
			return true;
		}
	}
	
}
