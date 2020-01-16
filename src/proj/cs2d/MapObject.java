package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class MapObject {
	private Rectangle rect;
	private Color color = null;
	private Image img = null;
		
	public MapObject(Image img) {
		this.img = img;
		this.rect = new Rectangle(img.getWidth(null), img.getHeight(null));
	}
	
	public MapObject(Image img, Rectangle bounds) {
		this.img = img;
		this.rect = bounds;
	}
	
	public MapObject(Color color, Rectangle bounds) {
		this.color = color;
		this.rect = bounds;
	}
	
	public Rectangle getBounds() {
		return this.rect;
	}
	
	public void render(Graphics2D g2d) {
		if(color != null) {
			g2d.setColor(color);
			g2d.fillRect(rect.x, rect.y, rect.width, rect.height);
		} else if(img != null) {
			g2d.drawImage(img, rect.x, rect.y, rect.x + rect.width, rect.y + rect.height, 0, 0, img.getWidth(null), img.getHeight(null), null);
		}
	}
}
