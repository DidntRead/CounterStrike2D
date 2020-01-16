package proj.cs2d;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class Quadtree {
	private int MAX_OBJECTS = 5000;
	private int MAX_LEVELS = 10;
	
	private int level;
	private List<MapObject> objects;
	private Rectangle bounds;
	private Quadtree[] nodes;
	
	public Quadtree(Rectangle bounds) {
		this(0, bounds);
	}
	
	private Quadtree(int level, Rectangle bounds) {
		this.level = level;
		objects = new ArrayList<>();
		this.bounds = bounds;
		nodes = new Quadtree[4];
	}
	
	public void clear() {
		objects.clear();
		
		for(int i = 0; i < nodes.length; i++) {
			if(nodes[i] != null) {
				nodes[i].clear();
				nodes[i] = null;
			}
		}
	}
	
	private void split() {
		int subWidth = (int)(bounds.getWidth() / 2);
		int subHeight = (int)(bounds.getHeight() / 2);
		int x = (int)bounds.getX();
		int y = (int)bounds.getY();
		 
		nodes[0] = new Quadtree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
		nodes[1] = new Quadtree(level+1, new Rectangle(x, y, subWidth, subHeight));
		nodes[2] = new Quadtree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
		nodes[3] = new Quadtree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}
	
	private int getIndex(Rectangle rect) {
		   int index = -1;
		   double verticalMidpoint = bounds.getX() + (bounds.getWidth() / 2);
		   double horizontalMidpoint = bounds.getY() + (bounds.getHeight() / 2);
		 
		   boolean topQuadrant = (rect.getY() < horizontalMidpoint && rect.getY() + rect.getHeight() < horizontalMidpoint);
		   boolean bottomQuadrant = (rect.getY() > horizontalMidpoint);
		 
		   if (rect.getX() < verticalMidpoint && rect.getX() + rect.getWidth() < verticalMidpoint) {
		      if (topQuadrant) {
		        index = 1;
		      }
		      else if (bottomQuadrant) {
		        index = 2;
		      }
		    }

		    else if (rect.getX() > verticalMidpoint) {
		     if (topQuadrant) {
		       index = 0;
		     }
		     else if (bottomQuadrant) {
		       index = 3;
		     }
		   }
		 
		   return index;
	}
	
	private int getIndex(MapObject obj) {
		return getIndex(obj.getBounds());
	}
	
	 public void insert(MapObject obj) {
		   if (nodes[0] != null) {
		     int index = getIndex(obj);
		 
		     if (index != -1) {
		       nodes[index].insert(obj);
		 
		       return;
		     }
		   }
		 
		   objects.add(obj);
		 
		   if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
		      if (nodes[0] == null) { 
		         split();
		      }
		 
		     int i = 0;
		     while (i < objects.size()) {
		       int index = getIndex(objects.get(i));
		       if (index != -1) {
		         nodes[index].insert(objects.remove(i));
		       }
		       else {
		         i++;
		       }
		     }
		   }
		 }
	 
	 public List<MapObject> retrieve(List<MapObject> returnObjects, Rectangle pRect) {
		   int index = getIndex(pRect);
		   if (index != -1 && nodes[0] != null) {
		     nodes[index].retrieve(returnObjects, pRect);
		   }
		 
		   returnObjects.addAll(objects);
		 
		   return returnObjects;
	}
	 
	 
}
