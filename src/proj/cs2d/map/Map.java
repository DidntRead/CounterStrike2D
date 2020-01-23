package proj.cs2d.map;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import proj.cs2d.Camera;
import proj.cs2d.Player;
import proj.cs2d.collision.Quadtree;
import proj.cs2d.map.editor.MapPanel;

public class Map implements Serializable {
	private static final long serialVersionUID = -5884999978757486798L;
	protected Quadtree tree;
	protected List<Updatable> updatable;
	protected MapObject spawnPoint0, spawnPoint1;
	protected int size;

	public Map(int size) {
		this.tree = new Quadtree(size);
		this.size = size;
		this.updatable = new ArrayList<Updatable>();
	}
	
	public Map(int size, List<MapObject> map) {
		this(size);
		for(MapObject obj : map) {
			add(obj);
		}
	}
	
	public void update(double delta) {
		for(Updatable updatable : updatable) {
			updatable.update(delta);
		}
	}
	
	public void add(MapObject obj) {
		if(obj instanceof Updatable) updatable.add((Updatable) obj);
		this.tree.insert(obj);
	}
	
	public void remove(MapObject obj) {
		if(obj instanceof Updatable) updatable.remove((Updatable) obj);
		this.tree.remove(obj);
	}
	
	public void setSpawnPoint(int team, MapObject spawnPoint) {
		if(team == 0) spawnPoint0 = spawnPoint;
		else spawnPoint1 = spawnPoint;
	}
	
	public Point getSpawnPosition(int team) {
		int x,y;
		if(team == 0) {
			do {
				x = ThreadLocalRandom.current().nextInt(spawnPoint0.bounds.x + spawnPoint0.bounds.width - 70) + 35;
				y = ThreadLocalRandom.current().nextInt(spawnPoint0.bounds.y + spawnPoint0.bounds.height - 58) + 29;
			} while(collide(new Rectangle(x, y, 35, 29), null));
		} else {
			do {
				x = ThreadLocalRandom.current().nextInt(spawnPoint1.bounds.x + spawnPoint1.bounds.width - 70) + 35;
				y = ThreadLocalRandom.current().nextInt(spawnPoint1.bounds.y + spawnPoint1.bounds.height - 58) + 29;
			} while(collide(new Rectangle(x, y, 35, 29), null));
		}
		return new Point(x, y);
	}
	
	public int getSize() {
		return this.size;
	}
	
	public void render(Graphics2D g2d, Rectangle bounds) {
		List<MapObject> objects = tree.getAllCollision(bounds);
		//List<MapObject> objects = tree.getAll();
		for(MapObject obj : objects) {
			if(obj instanceof RenderableMapObject) {
				((RenderableMapObject) obj).render(g2d);
			}
		}
	}
	
	public static void drawCenteredString(Graphics2D g2d, String s, int x, int y, int width, int height) {
	    FontMetrics metrics = g2d.getFontMetrics();
	    int drawX = x + (width - metrics.stringWidth(s)) / 2;
	    int drawY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g2d.setColor(Color.black);
	    g2d.drawString(s, drawX, drawY);
	}
	
	public void renderMapEditor(Graphics2D g2d, Camera camera) {
		List<MapObject> objects = tree.getAll();
		if(spawnPoint0 != null) {
			g2d.drawRect(spawnPoint0.getX(), spawnPoint0.getY(), spawnPoint0.getWidth(), spawnPoint0.getHeight());
			drawCenteredString(g2d, "Team 0", spawnPoint0.getX(), spawnPoint0.getY(), spawnPoint0.getWidth(), spawnPoint0.getHeight() / 2);
		}
		if(spawnPoint1 != null) {
			g2d.drawRect(spawnPoint1.getX(), spawnPoint1.getY(), spawnPoint1.getWidth(), spawnPoint1.getHeight());
			drawCenteredString(g2d, "Team 1", spawnPoint1.getX(), spawnPoint1.getY(), spawnPoint1.getWidth(), spawnPoint1.getHeight() / 2);
		}
		for(MapObject obj : objects) {
			if(obj instanceof RenderableMapObject) {
				((RenderableMapObject) obj).render(g2d);
			} else {
				g2d.drawRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
			}
		}
	}
	
	public boolean collide(Rectangle rect, Player player) {
		if(rect.x < 0 || rect.y < 0 || rect.x > size || rect.y > size) return true;
		List<MapObject> objects = tree.getAllCollision(rect);
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
	
	public MapObject getSpawnPoint(int team) {
		return team == 0 ? spawnPoint0 : spawnPoint1;
	}
	
	public MapObject collide(Point p) {
		List<MapObject> objects = tree.getAllCollision(p);
		for(MapObject obj : objects) {
			if(obj.isCollidable()) {
				if(obj.getBounds().contains(p)) return obj;
			}
		}
		return null;
	}
	
	public MapObject collideMapEditor(Point p) {
		List<MapObject> objects = tree.getAllCollision(p);
		if(spawnPoint0 != null) objects.add(spawnPoint0);
		if(spawnPoint1 != null) objects.add(spawnPoint1);
		for(MapObject obj : objects) {
			if(obj.getBounds().contains(p)) return obj;
		}
		return null;
	}
	
	public List<MapObject> collideMapEditor(Rectangle rect) {
		List<MapObject> objects =  tree.getAllCollision(rect);
		List<MapObject> ret = new ArrayList<MapObject>();
		for(MapObject obj : objects) {
			if(rect.intersects(obj.getBounds())) ret.add(obj);
		}
		return ret;
	}
	
	public MapObject collideView(Point p) {
		List<MapObject> objects = tree.getAllCollision(p);
		for(MapObject obj : objects) {
			if(obj.isCollidable() && !(obj instanceof RemotePlayer)) {
				if(obj.getBounds().contains(p)) return obj;
			}
		}
		return null;
	}
	
	public List<MapObject> getAll() {
		List<MapObject> obj =  this.tree.getAll();
		obj.add(spawnPoint0);
		obj.add(spawnPoint1);
		return obj;
	}
}
