package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

public class Map {
	private int sizeX, sizeY;
	private Quadtree quadtree;
	private Rectangle renderWindow;
	
	public Map(int sizeX, int sizeY, int windowWidth, int windowHeight) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.quadtree = new Quadtree(new Rectangle(sizeX, sizeY));
		this.renderWindow = new Rectangle(windowWidth, windowHeight);
				
		Random random = new Random(1000011);
		for(int i = 0; i < 5000; i++) {
			this.quadtree.insert(new MapObject(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)), new Rectangle(random.nextInt(5000), random.nextInt(5000), 50, 50)));
		}
	}

	public void renderWindow(int width, int height) {
		this.renderWindow = new Rectangle(width, height);
	}
	
	public void render(Graphics2D g2d, Player player) {
		ArrayList<MapObject> objects = new ArrayList<MapObject>();
		AffineTransform trans = g2d.getTransform();
		g2d.translate(player.getBounds().x, player.getBounds().y);
		objects = (ArrayList<MapObject>) quadtree.retrieve(objects, renderWindow);
		System.out.println(objects.size() + " to render");
		for(MapObject obj : objects) {
			obj.render(g2d);
		}
		g2d.setTransform(trans);
	}
	
	public void collide(Player player) {
		ArrayList<MapObject> objects = new ArrayList<MapObject>();
		objects = (ArrayList<MapObject>) quadtree.retrieve(objects, player.getBounds());
		System.out.println(objects.size() + " to check");
	}
	
	public int getWidth() {
		return this.sizeX;
	}
	
	public int getHeight() {
		return this.sizeY;
	}
}
