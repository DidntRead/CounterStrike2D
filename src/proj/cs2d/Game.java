package proj.cs2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;

import javax.imageio.ImageIO;

import proj.cs2d.map.Map;
import proj.cs2d.map.RemotePlayer;

public class Game {
	public static final int enableViewRectangle = 0;
	public static final boolean enableFastRenderingHints = false;
	
	private Window window;
	private BufferStrategy bufferStrategy;
	private Timer deltaTimer;
	private Player player;
	private Camera camera;
	private int team;
	private Map map;
	
	public Game(Map map, int team) {
		this.window = new Window();
		this.deltaTimer = new Timer(); 
		this.map = map;
		this.team = team;
	}
		
	public void start() {
		window.setVisible(true);
		window.createBufferStrategy(2);
		bufferStrategy = window.getBufferStrategy();
		player = new Player(map, team);
		camera = new Camera(player, window.getWidth(), window.getHeight());
		
		// Crosshair
		try {
			window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(ImageIO.read(Game.class.getResourceAsStream("/crosshair.png")), new Point(16, 16), "Crosshair"));
		} catch (HeadlessException | IndexOutOfBoundsException | IOException e2) {}
	
		this.window.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				camera.resize(window.getWidth(), window.getHeight());
			}
		});
		
		this.window.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				player.aim(e.getX(), e.getY(), camera);
			}
			@Override
			public void mouseDragged(MouseEvent e) {
				player.aim(e.getX(), e.getY(), camera);
			}
		});
		
		this.window.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				player.shoot(map);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				player.mouseHeldDown(true);
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				player.mouseHeldDown(false);
			}
		});
		
		this.window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				window.setVisible(false);
				window.dispose();
			}
		});
				
		this.window.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					if(player.velocityY >= 0) {
						player.velocityY = -player.getSpeed();
					}
					break;
				case KeyEvent.VK_S:
					if(player.velocityY <= 0) {
						player.velocityY = player.getSpeed();
					}
					break;
				case KeyEvent.VK_A:
					if(player.velocityX >= 0) {
						player.velocityX = -player.getSpeed();
					}
					break;
				case KeyEvent.VK_D:
					if(player.velocityX <= 0) {
						player.velocityX = player.getSpeed();
					}
					break;
				case KeyEvent.VK_SHIFT:
					player.sneak(true);
					break;
				case KeyEvent.VK_R:
					player.reload();
					break;
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_W:
					if(player.velocityY < 0) player.velocityY = 0;
					break;
				case KeyEvent.VK_S:
					if(player.velocityY > 0) player.velocityY = 0;
					break;
				case KeyEvent.VK_A:
					if(player.velocityX < 0) player.velocityX = 0;
					break;
				case KeyEvent.VK_D:
					if(player.velocityX > 0) player.velocityX = 0;
					break;
				case KeyEvent.VK_SHIFT:
					player.sneak(false);
					break;
				}
			}
		});
		
		RenderingHints hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				
		while(window.isShowing()) {
			Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
			
			if(enableFastRenderingHints) g2d.addRenderingHints(hints); 
			
			if(enableViewRectangle == 1) g2d.setBackground(Color.black);
			g2d.clearRect(0, 0, 600, 600);
			
			double delta = deltaTimer.elapsed();

			player.update(delta, camera, map);
			
			map.update(delta);
			
			camera.apply(g2d);
			
			// Background
			if(enableViewRectangle == 1) {
				g2d.setColor(new Color(238, 238, 238));
				g2d.fillRect(player.getX() - camera.getWidth() / 2, player.getY() - camera.getHeight() / 2, camera.getWidth(), camera.getHeight());
			}

			map.render(g2d, new Rectangle(player.getX() - camera.getWidth() / 2, player.getY() - camera.getHeight() / 2, camera.getWidth(), camera.getHeight()));

			player.render(g2d);
			
			camera.reverse(g2d);
			
			// Health
			g2d.setColor(Color.RED);
			g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 20f));
			g2d.drawString(String.valueOf(player.getHealth()), 10, camera.getHeight() - 10);
			
			// Reload indicator
			if(player.isReloading()) {
				g2d.setColor(Color.black);
				g2d.fillRect(camera.getWidth() / 2 - 50, camera.getHeight() / 2 + 30, 140, 20);
				g2d.setColor(Color.green);
				g2d.fillRect(camera.getWidth() / 2 - 50, camera.getHeight() / 2 + 30, (int) (140 * player.reloadRemaining()), 20);
				g2d.drawString("RELOADING...", camera.getWidth() / 2 - 50, camera.getHeight() / 2 + 20);
			}
			
			// Ammo
			g2d.setColor(Color.orange);
			g2d.drawString(player.getAmmoLeft() + "/" + player.getClipSize(), camera.getWidth() - 60, camera.getHeight() - 10);
			
			g2d.dispose();
			bufferStrategy.show();
			
			try {
				Thread.sleep(25);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
