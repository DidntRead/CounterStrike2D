package proj.cs2d.map;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WoodBox extends RenderableMapObject {
	private static Image img = null;
	
	public WoodBox(int x, int y, int width, int height) {
		super(x, y, width, height, img == null ? loadImage() : img, true);
	}
	
	private static Image loadImage() {
		try {
			WoodBox.img = ImageIO.read(HealthPickup.class.getResourceAsStream("/wood_box.png"));
		} catch (IOException e) {
		}
		return WoodBox.img;
	}
	
	public static Image getImage() {
		if(img == null) {
			loadImage();
		}
		return img;
	}
}
