package proj.cs2d;

public class Timer {
	private long time;
	
	public Timer() {
		time = System.currentTimeMillis();
	}
	
	/**
	 * Reset timer
	 */
	public void reset() {
		time = System.currentTimeMillis();
	}
	
	/**
	 * Get elapsed time since last call to elapsed
	 * @return elapsed time in milliseconds
	 */
	public long elapsedMillis() {
		long temp = time;
		time = System.currentTimeMillis();
		return time - temp;
	}
	
	/**
	 * Get elapsed time since last call to elapsed
	 * @return elapsed time in seconds
	 */
	public double elapsed() {
		long temp = time;
		time = System.currentTimeMillis();
		return (time - temp) / 1000d;
	}
}
