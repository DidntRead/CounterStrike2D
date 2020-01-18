package proj.cs2d.map.editor;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JPanel;

import proj.cs2d.Camera;
import proj.cs2d.map.Map;

public class MapPanel extends JPanel {	
	private Camera camera;
	private int horizontal,vertical;
	
	public MapPanel() {
		this.camera = new Camera(0, 0, 600, 600);
		enableEvents(MouseEvent.MOUSE_EVENT_MASK);
		enableEvents(MouseEvent.MOUSE_MOTION_EVENT_MASK);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(Editor.map.getSize(), Editor.map.getSize());
	}
	
	public void scrollHorizontal(int a) {
		horizontal = a;
	}
	
	@Override
	protected void processMouseEvent(MouseEvent e) {
	}
	
	@Override
	protected void processMouseMotionEvent(MouseEvent e) {
	}
	
	public void scrollVertical(int a) {
		vertical = a;
	}
	
	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.clearRect(0, 0, getWidth(), getHeight());
		camera.absoluteUpdate(-horizontal, -vertical);
		AffineTransform trans = g2d.getTransform();
		System.out.println(camera.toString());
		camera.apply(g2d);
		Editor.map.render(g2d, camera);
		g2d.setTransform(trans);
	};
}
