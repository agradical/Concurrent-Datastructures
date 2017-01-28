package Algorithms.task;

public class Counter extends Task {
	int counter;
	
	public Counter(int value) {
		counter = value;
		System.out.println(counter+": value");
	}
	@Override
	public void execute() {
		counter++;
		//System.out.println(counter);
	}

	@Override
	public void output() {
		System.out.println("Counter value: " + counter);
	}
}
