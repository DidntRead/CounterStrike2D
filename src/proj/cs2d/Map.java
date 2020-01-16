package proj.cs2d;

import java.awt.Graphics2D;

public class Map {
	private int sizeX, sizeY;
	
	public Map(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
	}

	public void render(Graphics2D g2d) {
		
	}
	
	public int getWidth() {
		return this.sizeX;
	}
	
	public int getHeight() {
		return this.sizeY;
	}
}
