package proj.cs2d.map;

import java.awt.Image;

import proj.cs2d.Player;

public class Pickup extends RenderableMapObject {
	private boolean active = true;
	
	public Pickup(int x, int y, int width, int height, Image img) {
		super(x, y, width, height, img , true);
		collidable = false;
	}
	
	public void pickedUp(Player player) {
		System.out.println("Override pickedUp to do something");
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void activate() {
		this.active = true;
	}
}
