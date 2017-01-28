package Queue;

public interface Operation {
	public boolean isEmpty();
	public void enqueue(int val);
	public int dequeue();
}
