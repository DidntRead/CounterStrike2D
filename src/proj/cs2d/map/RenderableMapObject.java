package proj.cs2d.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;

public class RenderableMapObject extends MapObject {
	protected Color color = null;
	protected Image img = null;
	protected boolean scale;
		
	public RenderableMapObject(int x, int y, int width, int height, Image img, boolean scale) {
		super(new Rectangle(x, y, width, height));
		this.img = img;
		this.scale = scale;
	}
	
	public RenderableMapObject(int x, int y, Image img, boolean scale) {
		super(new Rectangle(x, y, img.getWidth(null), img.getHeight(null)));
		this.img = img;
		this.scale = scale;
	}
	
	public RenderableMapObject(int x, int y, int width, int height, Image img) {
		this(x, y, width, height, img, true);
	}
	
	public RenderableMapObject(int x, int y, Image img) {
		this(x, y, img, true);
	}
		
	public RenderableMapObject(int x, int y, int width, int height, Color color) {
		super(new Rectangle(x, y, width, height));
		this.color = color;
	}
		
	public void render(Graphics2D g2d) {
		if(color != null) {
			g2d.setColor(color);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		} else {
			g2d.drawImage(img, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0, 0, scale ? img.getWidth(null) : bounds.width, scale ? img.getHeight(null) : bounds.height, null);
		}
	}
}
