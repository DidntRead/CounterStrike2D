package proj.cs2d;

import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

public class Camera {
	private int x, y;
	private int width, height;
	private int viewPolygonHeight = 900;
	private Polygon viewPolygon;
	
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
	
	public void updateViewPolygon(Player player) {
		int[] x = new int[3];
		int[] y = new int[3];
		
		double angle = player.getRotation() + Math.toRadians(45);
		double angle1 = player.getRotation() - Math.toRadians(195);
		double angle2 = player.getRotation() - Math.toRadians(75);
		
		
		x[1] = (int) (player.getCenterX() + 10 * Math.cos(angle));
		y[1] = (int) (player.getCenterY() + 10 * Math.sin(angle));
				
		x[0] = (int) (x[1] + viewPolygonHeight * Math.cos(angle1));
		y[0] = (int) (y[1] + viewPolygonHeight * Math.sin(angle1));
		
		x[2] = (int) (x[1] + viewPolygonHeight * Math.cos(angle2));
		y[2] = (int) (y[1] + viewPolygonHeight * Math.sin(angle2));
		
		this.viewPolygon = new Polygon(x, y, 3);
		
		System.out.println("View triangle");
		
		for(int i = 0; i < 3; i++) {
			System.out.println("X: " + x[i] + " Y: " + y[i]);
		}
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
		g2d.setClip(viewPolygon);
	}
	
	public void reverse(Graphics2D g2d) {
		g2d.translate(-x, -y);
		g2d.setClip(0, 0, width, height);
	}
}
