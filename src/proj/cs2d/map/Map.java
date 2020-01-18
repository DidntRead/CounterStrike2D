package proj.cs2d.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import proj.cs2d.Camera;
import proj.cs2d.Player;
import proj.cs2d.collision.Quadtree;

public class Map {
	protected Quadtree tree;
	protected MapObject spawnPoint0, spawnPoint1;
	protected int size;
	
	public Map(int size) {
		this.tree = new Quadtree(size);
		this.size = size;
	}
	
	public Map(int size, List<MapObject> map) {
		this(size);
		for(MapObject obj : map) {
			tree.insert(obj);
		}
	}
	
	public void setSpawnPoint(int team, MapObject spawnPoint) {
		if(team == 0) spawnPoint0 = spawnPoint;
		else spawnPoint1 = spawnPoint;
	}
	
	public Point getSpawnPosition(int team) {
		int x,y;
		if(team == 0) {
			x = ThreadLocalRandom.current().nextInt(spawnPoint0.bounds.x + spawnPoint0.bounds.width);
			y = ThreadLocalRandom.current().nextInt(spawnPoint0.bounds.y + spawnPoint0.bounds.height);
		} else {
			x = ThreadLocalRandom.current().nextInt(spawnPoint1.bounds.x + spawnPoint1.bounds.width);
			y = ThreadLocalRandom.current().nextInt(spawnPoint1.bounds.y + spawnPoint1.bounds.height);
		}
		return new Point(x, y);
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void render(Graphics2D g2d, Camera camera) {
		HashSet<MapObject> objects = tree.getAllCollision(camera.getBounds());
		for(MapObject obj : objects) {
			if(obj instanceof RenderableMapObject) {
				((RenderableMapObject) obj).render(g2d);
			}
		}
	}
	
	public boolean collide(Rectangle rect, Player player) {
		HashSet<MapObject> objects = tree.getAllCollision(rect);
		for(MapObject obj : objects) {
			if(obj.isCollidable()) {
				if(rect.intersects(obj.getBounds())) return true;
			} else if(obj instanceof Pickup) {
				if(rect.intersects(obj.getBounds())) {
					((Pickup)obj).pickedUp(player);
				}
			}
		}
		return false;
	}
	
	public MapObject collide(Point p) {
		HashSet<MapObject> objects = tree.getAllCollision(p);
		for(MapObject obj : objects) {
			if(obj.isCollidable()) {
				if(obj.getBounds().contains(p)) return obj;
			}
		}
		return null;
	}
}
