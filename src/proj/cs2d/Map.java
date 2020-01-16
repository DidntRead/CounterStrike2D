package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
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
		System.out.println(rect.toString());
		for(MapObject obj : coll) {
			if(obj.isCollidable()) {
				System.out.println(obj.getBounds().toString());
				if(checkForCollision(obj, rect)) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean checkForCollision(MapObject obj, Rectangle rect1) {
		Rectangle rect2 = obj.getBounds();
		return rect1.x < rect2.x + rect2.width && rect1.x + rect1.width > rect2.x && rect1.y < rect2.y + rect2.height && rect1.y + rect1.height > rect2.y;
	}
	
	public int getSize() {
		return this.size;
	}
}
