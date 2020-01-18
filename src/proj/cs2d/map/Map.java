package proj.cs2d.map;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
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
	
	public void update(float delta) {
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
		System.out.println(obj == null);
		this.tree.remove(obj);
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
	
	public static void drawCenteredString(Graphics2D g2d, String s, int x, int y, int width, int height) {
	    FontMetrics metrics = g2d.getFontMetrics();
	    int drawX = x + (width - metrics.stringWidth(s)) / 2;
	    int drawY = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g2d.setColor(Color.black);
	    g2d.drawString(s, drawX, drawY);
	}
	
	public void renderMapEditor(Graphics2D g2d, Camera camera) {
		HashSet<MapObject> objects = tree.getAllCollision(camera.getBounds());
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
		if(player.getBounds().x < 0 || player.getBounds().y < 0 || player.getBounds().x > size || player.getBounds().y > size) return true;
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
	
	public MapObject collideMapEditor(Point p) {
		HashSet<MapObject> objects = tree.getAllCollision(p);
		if(spawnPoint0 != null) objects.add(spawnPoint0);
		if(spawnPoint1 != null) objects.add(spawnPoint1);
		for(MapObject obj : objects) {
			if(obj.getBounds().contains(p)) return obj;
		}
		return null;
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
