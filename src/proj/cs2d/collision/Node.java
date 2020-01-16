package proj.cs2d.collision;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import proj.cs2d.MapObject;

public class Node {
	private Rectangle rect;
	private int level;
	private Node[] children;
	private List<MapObject> objects;
	
	protected Node(Rectangle rect, int level) {
		this.rect = rect;
		this.level = level;
		this.objects = new ArrayList<MapObject>();
		this.children = null;
	}
	
	protected void insert(MapObject obj) {
		if(isLeaf()) {
			if(this.objects.size() + 1 > Quadtree.MAX_OBJECTS && this.level <= Quadtree.MAX_LEVELS) {
				this.split();
				this.selectNode(obj.getBounds()).objects.add(obj);
			} else {
				this.objects.add(obj);
			}
		} else {
			Node node = selectNode(obj.getBounds());
			if(node == this) {
				this.objects.add(obj);
			} else {
				node.insert(obj);
			}
		}
	}
	
	private boolean isLeaf() {
		return children == null;
	}
	
	protected void getAll(List<MapObject> list) {
		list.addAll(objects);
		if(!isLeaf()) {
			children[0].getAll(list);
			children[1].getAll(list);
			children[2].getAll(list);
			children[3].getAll(list);
		}
	}
	
	private void split() {
		children = new Node[4];
		int halfWidth = rect.width / 2;
		int halfHeight = rect.height / 2;
		children[0] = new Node(new Rectangle(rect.x, rect.y, halfWidth, halfHeight), level + 1);
		children[1] = new Node(new Rectangle(rect.x + halfWidth, rect.y, halfWidth, halfHeight), level + 1);
		children[2] = new Node(new Rectangle(rect.x, rect.y + halfHeight, halfWidth, halfHeight), level + 1);
		children[3] = new Node(new Rectangle(rect.x + halfWidth, rect.y + halfHeight, halfWidth, halfHeight), level + 1);
	}
	
	protected void getAllIn(Rectangle rect, HashSet<MapObject> set) {
		if(this.rect.intersects(rect)) {
			set.addAll(objects);
			if(!isLeaf()) {
				children[0].getAllIn(rect, set);
				children[1].getAllIn(rect, set);
				children[2].getAllIn(rect, set);
				children[3].getAllIn(rect, set);
			}
		}
	}
	
	protected void getAllIn(Point p, HashSet<MapObject> set) {
		if(this.rect.contains(p)) {
			set.addAll(objects);
			if(!isLeaf()) {
				children[0].getAllIn(p, set);
				children[1].getAllIn(p, set);
				children[2].getAllIn(p, set);
				children[3].getAllIn(p, set);
			}
		}
	}
	
	private Node selectNode(Rectangle bound) {
		if(children[0].rect.contains(bound)) {
			return children[0];
		} else if(children[1].rect.contains(bound)) {
			return children[1];
		} else if(children[2].rect.contains(bound)) {
			return children[2];
		} else if(children[3].rect.contains(bound)) {
			return children[3];
		} else { 
			return this;
		}
	}
}
