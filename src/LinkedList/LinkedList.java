package LinkedList;

public class LinkedList {
	private Node HEAD;
	private Node TAIL;
	private static LinkedList ll;
	
	private LinkedList() {
		HEAD = new Node(0);
		TAIL = new Node(Integer.MAX_VALUE);
		HEAD.next = TAIL;
		HEAD.atomicNext.set(TAIL, false);
	}
	
	public static LinkedList getInstance() {
		if(ll == null) {
			ll = new LinkedList();
		}
		return ll;
	}
	
	public void print() {
		Node temp = HEAD.next;
		System.out.print("HEAD"+"->");
		while(temp != TAIL && temp != null) {
			System.out.print(temp.key+"->");
			temp = temp.next;
		}
		System.out.println("TAIL"+"->X");
	}
	
	public void printref() {
		Node temp = HEAD.atomicNext.getReference();
		System.out.print("HEAD"+"->");
		while(temp != TAIL && temp != null) {
			System.out.print(temp.key+"->");
			temp = temp.atomicNext.getReference();
		}
		System.out.println("TAIL"+"->X");
	}
	
	public boolean isConsistent(int mode) {
		if(mode == 0 || mode == 1) {
			Node temp = HEAD;
			int key = temp.key;
			while(temp != TAIL && temp != null) {
				temp = temp.next;
				int nxtkey = temp.key;
				if(nxtkey < key) {
					return false;
				} else {
					key = nxtkey;
				}
			}
			if(temp == TAIL) {
				return true;
			} else return false;
		} else {
			Node temp = HEAD;
			int key = temp.key;
			while(temp != TAIL && temp != null) {
				temp = temp.atomicNext.getReference();
				int nxtkey = temp.key;
				if(nxtkey < key) {
					return false;
				} else {
					key = nxtkey;
				}
			}
			if(temp == TAIL) {
				return true;
			} else return false;
		}
	}
	
	public boolean find(int key, int mode) {
		if(mode == 0 || mode == 1) {
			Node temp = HEAD;
			while(temp.key < key) {
				temp = temp.next;
			}
			if(temp.key != key) {
				return false;
			} else {
				return true;
			}
		} else {
			Node temp = HEAD.atomicNext.getReference();
			while(temp.key < key) {
				temp = temp.atomicNext.getReference();
			}
			if(temp.key != key) {
				return false;
			} else {
				return true;
			}
		}
	}
	
	public Node getHead() {
		return HEAD;
	}
	public Node getTail(){
		return TAIL;
	}
}
