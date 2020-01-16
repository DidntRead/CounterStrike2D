package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import proj.cs2d.collision.Quadtree;

public class Map {
	private int size;
	private Quadtree tree;
	
	public Map(int size) {
		this.size = size;
		this.tree = new Quadtree(size);
				
		Random random = new Random(1000011);
		for(int i = 0; i < 500; i++) {
			int x = random.nextInt(2500);
			int y = random.nextInt(2500);
			Color color = new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255));
			MapObject obj = new MapObject(color, new Rectangle(x, y, 50, 50), true);
			tree.insert(obj);
		}
	}
	
	public void render(Graphics2D g2d, Player player, int windowWidth, int windowHeight) {
		AffineTransform trans = g2d.getTransform();
		g2d.translate(player.getBounds().x, player.getBounds().y);
		//HashSet<MapObject> objects = tree.getAllCollision(new Rectangle(player.getBounds().x, player.getBounds().y, windowWidth, windowHeight));
		List<MapObject> objects = tree.getAll();
		for(MapObject obj : objects) {
			obj.render(g2d);
		}
		g2d.setTransform(trans);
	}
	
	public boolean collide(Rectangle rect, Player player) {
		rect.translate(player.getBounds().x, player.getBounds().y);
		HashSet<MapObject> coll = tree.getAllCollision(rect);
		for(MapObject obj : coll) {
			if(obj.isCollidable()) {
				if(rect.intersects(obj.getBounds())) {
					return true;
				}
			}
		}
		return false;
	}
	
	public MapObject collide(Point p, Player player) {
		p.translate(player.getBounds().x, player.getBounds().y);
		HashSet<MapObject> coll = tree.getAllCollision(p);
		for(MapObject obj : coll) {
			if(obj.isCollidable()) {
				if(obj.getBounds().contains(p)) return obj;
			}
		}
		return null;
	}
		
	public int getSize() {
		return this.size;
	}
}
