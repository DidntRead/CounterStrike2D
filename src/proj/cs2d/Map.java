package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;

public class Map {
	private int sizeX, sizeY;
	private Quadtree quadtree;
	private Rectangle renderWindow;
	
	public Map(int sizeX, int sizeY, int windowWidth, int windowHeight) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.quadtree = new Quadtree(new Rectangle(sizeX, sizeY));
		this.renderWindow = new Rectangle(windowWidth, windowHeight);
		
		this.quadtree.insert(new MapObject(Color.green, new Rectangle(200, 200, 200, 200)));
		this.quadtree.insert(new MapObject(Color.green, new Rectangle(100, 100, 100, 50)));
	}

	public void renderWindow(int width, int height) {
		this.renderWindow = new Rectangle(width, height);
	}
	
	public void render(Graphics2D g2d, Player player) {
		ArrayList<MapObject> objects = new ArrayList<MapObject>();
		AffineTransform trans = g2d.getTransform();
		g2d.translate(player.getBounds().x, player.getBounds().y);
		objects = (ArrayList<MapObject>) quadtree.retrieve(objects, renderWindow);
		for(MapObject obj : objects) {
			obj.render(g2d);
		}
		g2d.setTransform(trans);
	}
	
	public void collide(Player player) {
		ArrayList<MapObject> objects = new ArrayList<MapObject>();
		objects = (ArrayList<MapObject>) quadtree.retrieve(objects, player.getBounds());
		//TODO 
	}
	
	public int getWidth() {
		return this.sizeX;
	}
	
	public int getHeight() {
		return this.sizeY;
	}
}
