package Algorithms.algorithm;

public interface Algorithm extends Runnable {
	public void lock();
	public void unlock();
	public void output();
}
