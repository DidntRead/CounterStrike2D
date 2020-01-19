package proj.cs2d;

import java.awt.Graphics2D;
import java.awt.Rectangle;

public class Camera {
	private int x, y;
	private int width, height;
	
	public Camera(Player player, int width, int height) {
		this(-(player.getX() - width / 2), -(player.getY() - height / 2), width, height);
	}
	
	public Camera(int x, int y, int width, int height) {
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
	}
	
	@Override
	public String toString() {
		return String.format("X: %d, Y: %d, Width: %d, Height: %d", x, y, width, height);
	}
	
	public void resize(int newWidth, int newHeight) {
		this.width = newWidth;
		this.height = newHeight;
	}
	
	public void update(int x, int y) {
		this.x += x;
		this.y += y;
	}
	
	public void absoluteUpdate(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public int getY() {
		return this.y;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public Rectangle getBounds() {
		return new Rectangle(x, y, width, height);
	}
	
	public void apply(Graphics2D g2d) {
		g2d.translate(x, y);
	}
	
	public void reverse(Graphics2D g2d) {
		g2d.translate(-x, -y);
	}
}
