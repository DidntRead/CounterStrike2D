package proj.cs2d.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.imageio.ImageIO;

public class RenderableMapObject extends MapObject {
	protected Color color = null;
	protected transient Image img = null;
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
	
	public RenderableMapObject(int x, int y, int width, int height, Color color, boolean collidable) {
		super(new Rectangle(x, y, width, height), collidable);
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public boolean getScale() {
		return this.scale;
	}
	
	public void render(Graphics2D g2d) {
		if(color != null) {
			g2d.setColor(color);
			g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
		} else if (img != null) {
			g2d.drawImage(img, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0, 0, scale ? img.getWidth(null) : bounds.width, scale ? img.getHeight(null) : bounds.height, null);
		}
	}
	
	// Serialization
    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeBoolean(img != null);
        if(img != null) {
        	ImageIO.write((BufferedImage)img, "png", out);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        final boolean hasImage = in.readBoolean();
        if(hasImage) {
        	img = ImageIO.read(in);
        }
    }
}
