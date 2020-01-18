package proj.cs2d.map;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class HealthPickup extends Pickup {
	private static Image img = null;
	
	public HealthPickup(int x, int y, int width, int height, int healthRestoration) {
		super(x, y, width, height, img == null ? loadImage() : img, (player) -> {
			player.changeHealth(healthRestoration);
		});
	}
	
	private static Image loadImage() {
		try {
			HealthPickup.img = ImageIO.read(HealthPickup.class.getResourceAsStream("/health.png"));
		} catch (IOException e) {
		}
		return HealthPickup.img;
	}
	
	public static Image getImage() {
		if(img == null) {
			loadImage();
		}
		return img;
	}
}
