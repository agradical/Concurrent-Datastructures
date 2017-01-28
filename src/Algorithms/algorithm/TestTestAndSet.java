package Algorithms.algorithm;

public class TestTestAndSet extends TestAndSet implements Algorithm {
	
	@Override
	public void run() {
		super.run();
	}

	@Override
	public void lock() {
		while(true) {
			while(lock.get()) {};
			if(!lock.getAndSet(true)) {
				return;
			}
		}
	}

	@Override
	public void unlock() {
		lock.set(false);
	}

}
