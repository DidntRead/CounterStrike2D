package proj.cs2d.map.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import proj.cs2d.Camera;
import proj.cs2d.Game;
import proj.cs2d.map.HealthPickup;
import proj.cs2d.map.MapObject;
import proj.cs2d.map.RenderableMapObject;
import proj.cs2d.map.WoodBox;
import proj.cs2d.map.editor.command.AddCommand;
import proj.cs2d.map.editor.command.MoveCommand;
import proj.cs2d.map.editor.command.MultiRemoveCommand;
import proj.cs2d.map.editor.command.RemoveCommand;
import proj.cs2d.map.editor.command.ResizeCommand;
import proj.cs2d.map.editor.command.SpawnpointCommand;

public class MapPanel extends JPanel {
	private static final int resizePointRadius = 4;
	
	private CommandManager cmdManager;
	private Camera camera;
	private Editor editor;
	private int horizontal,vertical;
	private int startX, startY;
	private int pressedButton;
	private List<MapObject> editSelected;
	private Object data;
	private Object data2;
	private Direction direction = null;
	private Direction resizing = null;
	
	public MapPanel(Editor editor) {
		this.editor = editor;
		this.cmdManager = new CommandManager();
		this.camera = new Camera(0, 0, 1000, 1000);		
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				pressedButton = e.getButton();
				startX = e.getX() - camera.getX();
				startY = e.getY() - camera.getY();
				switch (pressedButton) {
					case MouseEvent.BUTTON1: {
						switch (editor.getChosen()) {
							case 0:
								List<Point[]> pointsList = getResizePoints();
								for(Point[] points : pointsList) {
									if(checkIfPointInside(points[0], startX, startY)) {
										setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
										data = editor.getEditedMap().collideMapEditor(new Point(startX, startY + 5));
										resizing = Direction.N;
									} else if(checkIfPointInside(points[1], startX, startY)) {
										setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
										data = editor.getEditedMap().collideMapEditor(new Point(startX + 5, startY));
										resizing = Direction.W;
									} else if(checkIfPointInside(points[2], startX, startY)) {
										setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
										data = editor.getEditedMap().collideMapEditor(new Point(startX - 5, startY));
										resizing = Direction.E;
									} else if(checkIfPointInside(points[3], startX, startY)) {
										setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
										data = editor.getEditedMap().collideMapEditor(new Point(startX, startY - 5));
										resizing = Direction.S;
									}
								}
									
								if(resizing != null) return;
									
								data = editor.getEditedMap().collideMapEditor(new Point(startX, startY));
								
								if(data != null) {
									if(e.isAltDown()) {
										if(editSelected != null) editSelected.remove((MapObject)data);
									} else {
										if(editSelected == null || !e.isControlDown()) editSelected = new ArrayList<MapObject>();
										editSelected.add((MapObject)data);
									}
								} else if(!e.isControlDown() && !e.isAltDown()) {
									editSelected = null;
								}

								editor.displayEdit(editSelected);
							break;
							
							case 1:
								data = new RenderableMapObject(startX, startY, 1, 1, editor.getChosenColor());
								break;
							case 2:
								data = new WoodBox(startX, startY, 1, 1);
								break;
							case 3:
								data = new HealthPickup(startX, startY, 1, 1, editor.getChosenHealthRestore(), editor.getChosenCooldown());
								break;
							case 4:
								data = new MapObject(new Rectangle(startX, startY, 1, 1), false);
								data2 = true;
								break;
						}
						break;
					}
					case MouseEvent.BUTTON2: {
						setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
						break;
					}
				}
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				int X = e.getX() - camera.getX();
				int Y = e.getY() - camera.getY();
				switch (pressedButton) {
					case MouseEvent.BUTTON3: {
						if(X == startX && Y == startY) {
							MapObject obj = editor.getEditedMap().collideMapEditor(new Point(startX, startY));
							if(obj != null) {
								cmdManager.execute(new RemoveCommand(editor.getEditedMap(), obj));
							}
						} else {
							cmdManager.execute(new MultiRemoveCommand(editor.getEditedMap(), editor.getEditedMap().collideMapEditor((Rectangle)data)));
						}
						break;
					}
					case MouseEvent.BUTTON1: {
						switch(editor.getChosen()) {
							case 0:
								if(resizing != null) {
									if(data != null && data2 != null) {
										MapObject obj = (MapObject) data;
										Rectangle origSize = (Rectangle) data2;
										ResizeCommand cmd = new ResizeCommand(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight(), obj);
										obj.setBounds(origSize);
										cmdManager.execute(cmd);
									}
								} else {
									if(data != null && data2 != null) {
										MapObject obj = (MapObject) data;
										Rectangle origSize = (Rectangle) data2;
										MoveCommand cmd = new MoveCommand(obj.getX(), obj.getY(), obj);
										obj.setBounds(origSize);
										cmdManager.execute(cmd);
									} else {
										if(data2 != null) {
											if(e.isControlDown()) {
												editSelected.addAll(editor.getEditedMap().collideMapEditor((Rectangle)data2));
											} else if(e.isAltDown()) {
												editSelected.removeAll(editor.getEditedMap().collideMapEditor((Rectangle)data2));
											} else {
												editSelected = editor.getEditedMap().collideMapEditor((Rectangle)data2);
											}
											editor.displayEdit(editSelected);
										}
									}
								}
							break;
							case 1:
							case 2:
							case 3:
								cmdManager.execute(new AddCommand(editor.getEditedMap(), (MapObject)data));
								break;
							case 4:
								cmdManager.execute(new SpawnpointCommand(editor.getEditedMap(), editor.getChosenTeam(), (MapObject)data));
								break;
						}
						break;
					}
				}
				pressedButton = 0;
				data = null;
				data2 = null;
				direction = null;
				resizing = null;
				setCursor(Cursor.getDefaultCursor());
				repaint();
			}
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				int X = e.getX() - camera.getX();
				int Y = e.getY() - camera.getY();
				
				boolean shiftChangedNow = false;
				if(e.isShiftDown() && direction == null) {
					shiftChangedNow = true;
					if(Y > X) {
						direction = Direction.N;
					} else {
						direction = Direction.E;
					}
				} else if(direction != null && !e.isShiftDown()) {
					shiftChangedNow = true;
					direction = null;
				}

				switch (pressedButton) {
					case 1: {
						switch (editor.getChosen()) {
						case 0:
							if(resizing != null) {
								MapObject obj = (MapObject) data;
								if(data2 == null) {
									data2 = new Rectangle(obj.getBounds());
								}
								switch(resizing) {
									case N:
										if(e.isControlDown()) {
											obj.setPosition(round(((Rectangle)data2).x - (startY - Y)), round(Y));
											obj.setSize((round(((Rectangle)data2).width + (startY - Y))), round(((Rectangle)data2).height + (startY - Y)));
										} else {
											obj.setPosition(obj.getX(), round(Y));
											obj.setSize(obj.getWidth(), round(((Rectangle)data2).height + (startY - Y)));
										}
										break;
									case E:
										if(e.isControlDown()) {
											obj.setSize((((Rectangle)data2).width + round(X - startX)), (((Rectangle)data2).height + round(X - startX)));
										} else {
											obj.setSize((((Rectangle)data2).width + round(X - startX)), obj.getHeight());
										}
										break;
									case S:
										if(e.isControlDown()) {
											obj.setSize(((Rectangle)data2).width + round(Y - startY), ((Rectangle)data2).height + round(Y - startY));
										} else {
											obj.setSize(obj.getWidth(), ((Rectangle)data2).height + round(Y - startY));
										}
										break;
									case W:
										if(e.isControlDown()) {
											obj.setPosition(round(X), round(((Rectangle)data2).y - (startX - X)));
											obj.setSize(round(((Rectangle)data2).width + (startX - X)), round(((Rectangle)data2).height + (startX - X)));
										} else {
											obj.setPosition(round(X), obj.getY());
											obj.setSize(round(((Rectangle)data2).width + (startX - X)), obj.getHeight());
										}
										break;
								}
							} else {
								if(data != null) {
									setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
									if(data2 == null) data2 = new Rectangle(((MapObject)data).getBounds());
									Rectangle orig = (Rectangle)data2;
									if(direction != null) {
										if(direction == Direction.N) {
											if(shiftChangedNow) orig.x = orig.x + round(X - startX);
											((MapObject)data).setPosition(orig.x, orig.y + round(Y - startY));
										} else {
											if(shiftChangedNow) orig.y = orig.y + round(Y - startY);
											((MapObject)data).setPosition(orig.x + round(X - startX), orig.y);
										}
									} else {
										if(shiftChangedNow) {
											orig.x = X - orig.width / 2;
											orig.y = Y - orig.height / 2;
											startX = X;
											startY = Y;
										}
										((MapObject)data).setPosition(orig.x + round(X - startX), orig.y + round(Y - startY));
									}
								} else {
									if(data2 == null) {
										data2 = new Rectangle(startX, startY, 1, 1);
									} else {
										Rectangle rect = (Rectangle)data2;
										rect.setSize(X - startX, Y - startY);
										if(X - startX < 0) {
											rect.setLocation(X, rect.y);
											rect.setSize(startX - X, rect.height);
										}
										if(Y - startY < 0) {
											rect.setLocation(rect.x, Y);
											rect.setSize(rect.width, startY - Y);
										}
									}
								}
							}
							break;
						case 1:
						case 2:
						case 3:
						case 4:
							MapObject obj = (MapObject)data;
							obj.setSize(round(X - startX), round(Y - startY));
							if(X - startX < 0) {
								obj.setPosition(round(X), obj.getY());
								obj.setSize(round(startX - X), obj.getHeight());
							}
							if(Y - startY < 0) {
								obj.setPosition(obj.getX(), round(Y));
								obj.setSize(obj.getWidth(), round(startY - Y));
							}
							break;
						}
						break;
					}
					case 2: {
						horizontal = startX - e.getX();
						vertical = startY - e.getY();
						break;
					}
					case 3: {
						if(data == null) {
							data = new Rectangle(startX, startY, 1, 1);
						} else {
							Rectangle rect = (Rectangle)data;
							rect.setSize(X - startX, Y - startY);
							if(X - startX < 0) {
								rect.setLocation(X, rect.y);
								rect.setSize(startX - X, rect.height);
							}
							if(Y - startY < 0) {
								rect.setLocation(rect.x, Y);
								rect.setSize(rect.width, startY - Y);
							}
						}
						break;
					}
				}
				repaint();
			}
		});
	}
	
	public boolean canUndo() {
		return this.cmdManager.canUndo();
	}
	
	public boolean canRedo() {
		return this.cmdManager.canRedo();
	}
	
	public void undo() {
		this.cmdManager.undo();
	}
	
	public void redo() {
		this.cmdManager.redo();
	}
	
	private boolean checkIfPointInside(Point p, int x, int y) {
		int dx = Math.abs(x - p.x);
		int dy = Math.abs(y - p.y);
		
		if(dx > resizePointRadius * 2) return false;
		if(dy > resizePointRadius * 2) return false;
		if(dx + dy <= resizePointRadius * 2) return true;
		return (dx * dx) + (dy * dy) <= ((resizePointRadius * 2) * (resizePointRadius * 2));
	}

	private int round(int n) {
		if(editor.getAlign() == 0) return n;
		int remainder = n % editor.getAlign();
		if(remainder == 0) return n;
		return n < 0 ? n - (editor.getAlign() + remainder) : n + (editor.getAlign() - remainder);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(editor.getEditedMap().getSize(), editor.getEditedMap().getSize());
	}
	
	@Override
	public boolean isFocusable() {
		return false;
	}
	
	public List<Point[]> getResizePoints() {
		List<Point[]> mids = new ArrayList<Point[]>();
		
		for (MapObject obj : editor.getEditedMap().getAll()) {
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
		editor.getEditedMap().renderMapEditor(g2d, camera);
		
		if(editor.getChosen() == 0) {
			List<Point[]> points = getResizePoints();
			g2d.setColor(Color.black);
			for(Point[] point : points) {
				g2d.drawOval(point[0].x, point[0].y, 2 * resizePointRadius, 2 * resizePointRadius);
				g2d.drawOval(point[1].x, point[1].y, 2 * resizePointRadius, 2 * resizePointRadius);
				g2d.drawOval(point[2].x, point[2].y, 2 * resizePointRadius, 2 * resizePointRadius);
				g2d.drawOval(point[3].x, point[3].y, 2 * resizePointRadius, 2 * resizePointRadius);
			}
		}
		
		if(editSelected != null) {
			Stroke orig = g2d.getStroke();
			g2d.setStroke(new BasicStroke(2));
			g2d.setColor(Color.black);
			for(MapObject obj : editSelected) {
				g2d.drawRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
			}
			g2d.setStroke(orig);
		}
		
		if(data != null) {
    		if(pressedButton == MouseEvent.BUTTON3) {
    			g2d.setColor(Color.red);
    	        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
    	        Stroke orig = g2d.getStroke();
    	        g2d.setStroke(dashed);
    			Rectangle rect = (Rectangle)data;
    			g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
    			g2d.setStroke(orig);
    		} else if(pressedButton == MouseEvent.BUTTON1) {
				g2d.setColor(Color.black);
    			if(data instanceof RenderableMapObject) {
    				((RenderableMapObject)data).render(g2d);
    			} else if(data2 instanceof Boolean) {
    				MapObject obj = (MapObject) data;
    				g2d.drawRect(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
    				drawCenteredString(g2d, editor.getChosenTeam() == 0 ? "Team 0" : "Team 1", new Rectangle(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight() / 2));
    			}
    			MapObject obj = (MapObject) data;
    			String s = String.format("%d %d %d %d", obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
    			drawCenteredString(g2d, s, obj.getBounds());
    		}
        } else if(data2 != null) {
        	if(pressedButton == MouseEvent.BUTTON1) {
    			g2d.setColor(Color.black);
    	        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
    	        Stroke orig = g2d.getStroke();
    	        g2d.setStroke(dashed);
    			Rectangle rect = (Rectangle)data2;
    			g2d.drawRect(rect.x, rect.y, rect.width, rect.height);
    			g2d.setStroke(orig);
        	}
        }
		
		g2d.setTransform(trans);
	};
	
	public CommandManager getCommandManager() {
		return this.cmdManager;
	}
}
