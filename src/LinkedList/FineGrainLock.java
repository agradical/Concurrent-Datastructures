package LinkedList;

public class FineGrainLock implements Operation {

	public LinkedList ll;
	
	public FineGrainLock() {
		this.ll = LinkedList.getInstance();
	}
	
	@Override
	public boolean contains(int val) {
		Node node = find(val);
		if(node.next.key == val && !node.next.mark) {
			return true;
		}
		return false;
	}
	
	public Node find(int val) {
		Node head = ll.getHead();
		Node pred = head;
		Node curr = pred.next;
		while(curr.key < val) {
			pred = curr;
			curr = curr.next;
		}
		return pred;
	}
	
	public boolean validate(Node pred, Node curr) {
		if(!pred.mark && !curr.mark && pred.next == curr ) {
			return true;
		} else return false;
	}
	
	@Override
	public boolean insert(int val) {
		Node node = new Node(val);
		while(true) {
			Node pred = find(val);
			Node curr = pred.next;
			
			pred.lock();
			curr.lock();
			if(validate(pred, curr)) {
				if(curr.key == val) {
					curr.unlock();
					pred.unlock();
					return false;
				}
				node.next = curr;
				pred.next = node;
				curr.unlock();
				pred.unlock();
				return true;
			} 
			curr.unlock();
			pred.unlock();	
		}
	}

	@Override
	public boolean remove(int val) {
		while(true) {
			Node pred = find(val);
			Node curr = pred.next;
			
			pred.lock();
			curr.lock();
			if(validate(pred, curr)) {
				if(curr.key == val) {
					curr.mark = true;
					pred.next = curr.next;
					curr.unlock();
					pred.unlock();
					return true;
				} else {
					curr.unlock();
					pred.unlock();
					return false;
				}
			}	
			curr.unlock();
			pred.unlock();
		}
	}

}
