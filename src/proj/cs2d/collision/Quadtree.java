package proj.cs2d.collision;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import proj.cs2d.MapObject;

public class Quadtree {
	protected final static int MAX_OBJECTS = 10;
	protected final static int MAX_LEVELS = 5;
	
	private Node root;
	
	public Quadtree(int size) {
		this.root = new Node(new Rectangle(0, 0, size, size), 0);
	}
	
	public void insert(MapObject obj) {
		root.insert(obj);
	}
	
	public List<MapObject> getAll() {
		ArrayList<MapObject> list = new ArrayList<MapObject>();
		root.getAll(list);
		return list;
	}
	
	public HashSet<MapObject> getAllCollision(Rectangle rect) {
		HashSet<MapObject> set = new HashSet<MapObject>(20);
		root.getAllIn(rect, set);
		return set;
	}
	
	public HashSet<MapObject> getAllCollision(Point p) {
		HashSet<MapObject> set = new HashSet<MapObject>(20);
		root.getAllIn(p, set);
		return set;
	}
}
