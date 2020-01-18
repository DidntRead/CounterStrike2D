package proj.cs2d.map;

import java.awt.Image;

import proj.cs2d.Player;

public class Pickup extends RenderableMapObject {
	private PickupFunc func;
	
	public Pickup(int x, int y, int width, int height, Image img, PickupFunc func) {
		super(x, y, width, height, img , true);
		collidable = false;
		this.func = func;
	}
	
	public void pickedUp(Player player) {
		func.pickedUp(player);
	}
}
