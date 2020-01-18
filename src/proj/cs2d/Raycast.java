package proj.cs2d;

import java.awt.Point;

public class Raycast {
	private Point current;
	private int length;
	private double rotation;
	private int speed;
	
	public Raycast(int x, int y, double rotation) {
		this(x, y, rotation, 20);
	}
	
	public Raycast(int x, int y, double rotation, int speed) {
		this.current = new Point(x, y);
		this.rotation = rotation - Math.toRadians(135);
		this.speed = speed;
	}
	
	public Point progress() {
		current.x += speed * Math.cos(rotation);
		current.y += speed * Math.sin(rotation);
		length += speed;
		return current;
	}
	
	public int getLength() {
		return this.length;
	}
}
