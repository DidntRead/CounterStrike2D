package proj.cs2d;

import java.awt.Graphics2D;
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

import proj.cs2d.map.Map;

public class Game {
	private java.awt.Window window;
	private BufferStrategy bufferStrategy;
	private Timer deltaTimer;
	private Player player;
	private Camera camera;
	private Map map;
	
	public Game(Map map) {
		this.window = new Window();
		this.deltaTimer = new Timer();
		this.map = map;
	}
		
	public void start() {
		window.setVisible(true);
		window.createBufferStrategy(2);
		bufferStrategy = window.getBufferStrategy();
		player = new Player(map, 0);
		camera = new Camera(player, window.getWidth(), window.getHeight());
				
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
		});
		
		this.window.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				player.shoot(map);
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
				}
			}
		});
		
		while(window.isShowing()) {
			Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
			g2d.clearRect(0, 0, 600, 600);
			float delta = deltaTimer.elapsed();
						
			player.update(delta, camera, map);
			
			camera.apply(g2d);
			
			player.render(g2d);
						
			map.render(g2d, camera);
			
			g2d.dispose();
			bufferStrategy.show();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		}
	}
}
