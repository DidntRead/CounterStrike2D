package proj.cs2d;

public class Cooldown {
	private long start;
	private int cooldown;
	
	/**
	 * Create new cooldown
	 * @param cooldown cooldown period in milliseconds
	 */
	public Cooldown(int cooldown) {
		this.cooldown = cooldown;
		reset();
	}
	
	/**
	 * Check if cooldown has passed
	 * @return true if it has passed false otherwise
	 */
	public boolean hasPassed() {
		return System.currentTimeMillis() > this.start + cooldown;
	}
	
	/**
	 * Reset cooldown
	 */
	public void reset() {
		this.start = System.currentTimeMillis();
	}
	
	/**
	 * Check if cooldown has passed and reset if it has
	 * @return true if it has passed false otherwise
	 */
	public boolean hasPassedReset() {
		if(System.currentTimeMillis() > this.start + cooldown) {
			start = System.currentTimeMillis();
			return true;
		}
		return false;
	}
}
