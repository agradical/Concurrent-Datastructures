package Queue;

public class LockFree implements Operation {

	public LinkedList ll;

	public  LockFree() {
		this.ll = LinkedList.getInstance();
	}
	
	@Override
	public boolean isEmpty() {
		return ll.HEADREF.get().next == null;
	}

	@Override
	public void enqueue(int val) {
		Node node = new Node(val);
		while(true) {
			
			Node last = ll.TAILREF.get();
			Node next = last.atomicNext.get();
			if(last == ll.TAILREF.get()) {
				if(next == null) {
					if(last.atomicNext.compareAndSet(next, node)) {
						ll.TAILREF.compareAndSet(last, node);
						return;	
					}
				} else {
					ll.TAILREF.compareAndSet(last, next);
				}
			}
		}
	}

	@Override
	public int dequeue() {
		while(true) {
			Node first = ll.HEADREF.get();
			Node last = ll.TAILREF.get();
			Node next  = first.atomicNext.get();
			
			if(first == ll.HEADREF.get()) {
				if(first == last) {
					if(next == null) {
						//return ERROR_CODE
						return -2;
					}
					ll.TAILREF.compareAndSet(last, next);
				} else {
					int val = next.key;
					if(ll.HEADREF.compareAndSet(first, next)) {
						return val;
					}
				}
			}
			
		}
	}
}
