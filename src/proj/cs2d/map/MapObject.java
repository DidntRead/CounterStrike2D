package proj.cs2d.map;

import java.awt.Rectangle;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class MapObject implements Serializable {
	protected Rectangle bounds;
	protected boolean collidable = true;
	
	public MapObject(Rectangle bounds) {
		this.bounds = bounds;
	}
	
	public MapObject(Rectangle bounds, boolean collidable) {
		this.bounds = bounds;
		this.collidable = collidable;
	}
	
	public void setCollidable(boolean v) {
		this.collidable = v;
	}
	
	public int getX() {
		return this.bounds.x;
	}
	
	public int getY() {
		return this.bounds.y;
	}
	
	public boolean isCollidable() {
		return this.collidable;
	}
	
	public int getWidth() {
		return this.bounds.width;
	}
	
	public int getHeight() {
		return this.bounds.height;
	}
	
	public void setPosition(int x, int y) {
		this.bounds.x = x;
		this.bounds.y = y;
	}
	
	public void setSize(int width, int height) {
		this.bounds.width = width;
		this.bounds.height = height;
	}
	
	public void changePosition(int x, int y) {
		this.bounds.x += x;
		this.bounds.y += y;
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
}
