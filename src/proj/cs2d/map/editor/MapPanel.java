package proj.cs2d.map.editor;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import proj.cs2d.Camera;
import proj.cs2d.map.HealthPickup;
import proj.cs2d.map.MapObject;
import proj.cs2d.map.RenderableMapObject;
import proj.cs2d.map.WoodBox;

public class MapPanel extends JPanel {	
	private Camera camera;
	private boolean mapDrag = false;
	private boolean componentCreate = false;
	private MapObject obj = null;
	static MapObject selectedObj = null;
	private int horizontal,vertical;
	private int startWidth,startHeight;
	private int startX, startY;
	private MovementDirection movement = null;
	private MovementDirection resizeDirection = null;
	private int startComponentX, startComponentY;
	
	int midPointX1, midPointY1, midPointX2, midPointY2, midPointX3, midPointY3, midPointX4, midPointY4;
	
	public MapPanel() {
		this.camera = new Camera(0, 0, 1000, 1000);		
		
		JComponent component = this;
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				int X = e.getX() + horizontal;
				int Y = e.getY() + vertical;
				if(e.getButton() == MouseEvent.BUTTON2) {
					mapDrag = true;
					startX = X;
					startY = Y;
				} else if(e.getButton() == MouseEvent.BUTTON3) {
					MapObject obj = Editor.map.collideMapEditor(new Point(X, Y));
					if(obj != null) {
						Editor.map.remove(obj);
					}
					repaint();
				} else if(e.getButton() == MouseEvent.BUTTON1) {
					componentCreate = true;
					startX = X;
					startY = Y;
					if(Editor.chosen == 0) {
						// Check for resize
						for(Point[] points : getMidPoints()) {
							if(checkIfPointInside(points[0], X, Y)) {
								setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
								obj = Editor.map.collideMapEditor(new Point(startX, startY + 5));
								resizeDirection = MovementDirection.N;
							} else if(checkIfPointInside(points[1], X, Y)) {
								setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
								obj = Editor.map.collideMapEditor(new Point(startX + 5, startY));
								resizeDirection = MovementDirection.W;
							} else if(checkIfPointInside(points[2], X, Y)) {
								setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
								obj = Editor.map.collideMapEditor(new Point(startX - 5, startY));
								resizeDirection = MovementDirection.E;
							} else if(checkIfPointInside(points[3], X, Y)) {
								setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
								obj = Editor.map.collideMapEditor(new Point(startX, startY - 5));
								resizeDirection = MovementDirection.S;
							}
							
							if(resizeDirection != null) {
								startWidth = obj.getWidth();
								startHeight = obj.getHeight();
								return;
							}
						}
						
						obj = Editor.map.collideMapEditor(new Point(startX, startY));
						if(obj != null) {
							selectedObj = obj;
							startComponentX = obj.getX();
							startComponentY = obj.getY();
							updateInfo();
						}
					} else if(Editor.chosen == 1 || Editor.chosen == 2 || Editor.chosen == 3 || Editor.chosen == 4) {
						startX = round(startX);
						startY = round(startY);
						int size = Editor.align == 0 ? 1 : Editor.align;
						switch (Editor.chosen) {
						case 1:
							obj = new RenderableMapObject(startX, startY, size, size, Editor.chosenColor);
							obj.setCollidable(true);
							break;
						case 2:
							obj = new WoodBox(startX, startY, size, size);
							obj.setCollidable(true);
							break;
						case 3:
							obj = new HealthPickup(startX, startY, size, size, Editor.restoreAmount, Editor.cooldown);
							break;
						}
						if(Editor.chosen != 4) {
							Editor.map.add(obj);
						} else {
							obj = new MapObject(new Rectangle(startX, startY, size, size));
							Editor.map.setSpawnPoint(Editor.chosenTeam, obj);
						}
						repaint();
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				setCursor(Cursor.getDefaultCursor());
				if(e.getButton() == MouseEvent.BUTTON2) {
					mapDrag = false;
				} else if(e.getButton() == MouseEvent.BUTTON1) {
					componentCreate = false;
					resizeDirection = null;
					movement = null;
					obj = null;
					repaint();
				}
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int X = e.getX() + horizontal;
				int Y = e.getY() + vertical;
				
				if(mapDrag) {
                    JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, component);
                    
                    Rectangle view = viewPort.getViewRect();
                    view.x += startX - X;
                    view.y += startY - Y;
                    
					scrollRectToVisible(view);
				} else if(componentCreate) {
					if(Editor.chosen == 0) {
						if(resizeDirection != null) {
							switch (resizeDirection) {
							case N:
								obj.setPosition(obj.getX(), round(Y));
								obj.setSize(obj.getWidth(),round(startHeight + (startY - Y)));
								break;
							case E:
								obj.setSize(round(startWidth + X - startX), obj.getHeight());
								break;
							case S:
								obj.setSize(obj.getWidth(), round(startHeight + Y - startY));
								break;
							case W:
								obj.setPosition(round(X), obj.getY());
								obj.setSize(round(startWidth + (startX - X)), obj.getHeight());
								break;
							}
							repaint();
						} else if(obj != null) {
							setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
							int changeX = (X - startX);
							int changeY = (Y - startY);
							if(e.isShiftDown()) {
								if(movement == null) {
									if(changeX > changeY) movement = MovementDirection.HORIZONTAL;
									else movement = MovementDirection.VERTICAL;
								}
								if(movement == MovementDirection.HORIZONTAL) {
									changeY = 0;
								} else if(movement == MovementDirection.VERTICAL) {
									changeX = 0;
								}
							}
							obj.setPosition(round(startComponentX + changeX), round(startComponentY + changeY));
							repaint();
						}
					} else if(Editor.chosen == 1 || Editor.chosen == 2 || Editor.chosen == 3 || Editor.chosen == 4) {
						obj.setSize(round(X - startX), e.isControlDown() ? round(X - startX) : round(Y - startY));
						if(startX > X) {
							obj.setPosition(round(X), obj.getY());
							obj.setSize(round(startX - X), e.isControlDown() ? round(startX - X) : obj.getHeight());
						}
						if(startY > Y) {
							obj.setPosition(obj.getX(), round(Y));
							obj.setSize(e.isControlDown() ? round(startY - Y) : obj.getWidth(), round(startY - Y));
						}
						repaint();
					}
				}
			}
		});
	}
	
	private void updateInfo() {
		if(selectedObj instanceof RenderableMapObject) {
			RenderableMapObject obj = (RenderableMapObject) selectedObj;
			if(obj.getColor() != null) {
				Editor.lblColor.setVisible(true);
				Editor.btnChange.setBackground(obj.getColor());
				Editor.btnChange.setVisible(true);
			} else {
				Editor.lblColor.setVisible(false);
				Editor.btnChange.setVisible(false);
			}
		} else {
			Editor.lblColor.setVisible(false);
			Editor.btnChange.setVisible(false);
		}
		Editor.checkCollidable.setSelected(obj.isCollidable());
	}
	
	private boolean checkIfPointInside(Point p, int x, int y) {
		int dx = Math.abs(x - p.x);
		int dy = Math.abs(y - p.y);
		int r = 8;
		
		if(dx > r) return false;
		if(dy > r) return false;
		if(dx + dy <= r) return true;
		return (dx * dx) + (dy * dy) <= (r * r);
	}

	private int round(int n) {
		if(Editor.align == 0) return n;
		int remainder = n % Editor.align;
		if(remainder == 0) return n;
		return n + Editor.align - remainder;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Editor.map.getSize(), Editor.map.getSize());
	}
	
	@Override
	public boolean isFocusable() {
		return false;
	}
	
	public List<Point[]> getMidPoints() {
		List<Point[]> mids = new ArrayList<Point[]>();
		
		for (MapObject obj : Editor.map.getAll()) {
			Point[] points = new Point[4];
			int x = obj.getX() - 4;
			int y = obj.getY() - 4;
			points[0] = new Point(x + obj.getWidth() / 2, y);
			points[1] = new Point(x, y + obj.getHeight() / 2);
			points[2] = new Point(x + obj.getWidth(), y + obj.getHeight() / 2);
			points[3] = new Point(x + obj.getWidth() / 2, y + obj.getHeight());
			mids.add(points);
		}
		
		return mids;
	}
	
	public void scrollHorizontal(int a) {
		horizontal = a;
	}
		
	public void scrollVertical(int a) {
		vertical = a;
	}
	
	private static void drawCenteredString(Graphics2D g2d, String s, Rectangle rect) {
	    FontMetrics metrics = g2d.getFontMetrics();
	    int x = rect.x + (rect.width - metrics.stringWidth(s)) / 2;
	    int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
	    g2d.setColor(Color.black);
	    g2d.drawString(s, x, y);
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		camera.absoluteUpdate(-horizontal, -vertical);
		AffineTransform trans = g2d.getTransform();
		camera.apply(g2d);
		Editor.map.renderMapEditor(g2d, camera);
		if(Editor.chosen == 0) {
			g2d.setColor(Color.black);
			for(Point[] midPoints : getMidPoints()) {
				g2d.fillOval(midPoints[0].x, midPoints[0].y, 8, 8);
				g2d.fillOval(midPoints[1].x, midPoints[1].y, 8, 8);
				g2d.fillOval(midPoints[2].x, midPoints[2].y, 8, 8);
				g2d.fillOval(midPoints[3].x, midPoints[3].y, 8, 8);
			}
		}
		if(obj != null) {
			String s = String.format("%d %d %d %d", obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
			drawCenteredString(g2d, s, obj.getBounds());
		}
		g2d.setTransform(trans);
	};
}
