package proj.cs2d.map;

import java.awt.Rectangle;

public class MapObject {
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
	
	public boolean isCollidable() {
		return this.collidable;
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
}
