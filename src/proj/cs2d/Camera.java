package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;

import proj.cs2d.map.Map;

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
	
	public void updateViewPolygon(Player player, Map map) {
		int raycastCount = 90;
		double coneAngle = Math.toRadians(90);
		
		int[] x = new int[raycastCount + 1];
		int[] y = new int[raycastCount + 1];
		
		double centerAngle = player.getRotation() + Math.toRadians(15);
		
		double reverseAngle = centerAngle + Math.toRadians(30);

		x[0] = (int) (player.getCenterX() + 10 * Math.cos(reverseAngle));
		y[0] = (int) (player.getCenterY() + 10 * Math.sin(reverseAngle));
		
		int startX = player.getCenterX();
		int startY = player.getCenterY();
		
		int index = 1;
		for(double angle = centerAngle - coneAngle / 2; angle < centerAngle + coneAngle / 2 && index < raycastCount + 1; angle += coneAngle / raycastCount, index++) {
			Raycast raycast = new Raycast(startX, startY, angle, 20);
			while(raycast.getLength() < viewPolygonHeight) {
				Point p = raycast.progress();
				x[index] = p.x;
				y[index] = p.y;
				if(map.collideView(p) != null) {
					break;
				}
			}
		}
		
		System.out.println("View polygon");
		for(int i = 0; i < raycastCount + 1; i++) {
			System.out.println("X: " + x[i] + " Y: " + y[i]);
		}
		
		this.viewPolygon = new Polygon(x, y, raycastCount + 1);
		
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
		if(Game.enableViewRectangle == 1) {
			g2d.setClip(viewPolygon);
		} else if(Game.enableViewRectangle == 2) {
			g2d.setColor(Color.red);
			g2d.drawPolygon(viewPolygon);
		}
	}
	
	public void reverse(Graphics2D g2d) {
		g2d.translate(-x, -y);
		g2d.setClip(0, 0, width, height);
	}
}
