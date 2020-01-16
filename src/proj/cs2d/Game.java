package proj.cs2d;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferStrategy;
import java.net.InetAddress;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class Game extends JFrame implements Runnable,KeyListener,MouseMotionListener,MouseListener {
	public static final int PORT = 28852;
	
	private static Game game;
	private static int width, height;
	private JPanel contentPane;
	private static boolean running = true;
	Player player = new Player(300, 300);

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// Crosshair
					Image cursor = ImageIO.read(Game.class.getResourceAsStream("/crosshair.png"));
					Cursor crosshair = Toolkit.getDefaultToolkit().createCustomCursor(cursor, new Point(16, 16), "Crosshair");
					
					// Server - addr(server address), port(server port)
					String servAddr = JOptionPane.showInputDialog(null, "Enter server address: ");
					int port = PORT;
					InetAddress addr;
					if(servAddr.contains(":")) {
						String[] info = servAddr.split(":");
						port = Integer.valueOf(info[1]);
						addr = InetAddress.getByName(info[0]);
					} else {
						addr = InetAddress.getByName(servAddr);
					}
					
					game = new Game();
					game.setCursor(crosshair);
					game.setVisible(true);
					game.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							running = false;
						}
					});
					game.addComponentListener(new ComponentAdapter() {
						@Override
						public void componentResized(ComponentEvent e) {
							width = game.getWidth();
							height = game.getHeight();
						}
					});
					game.addKeyListener(game);
					game.addMouseMotionListener(game);
					game.addMouseListener(game);
					new Thread(game).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Game() {
		setTitle("CS 2D");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 600, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
	}
	
	@Override
	public void run() {
		Timer timer = new Timer();
		game.createBufferStrategy(2);
		BufferStrategy bufferStrategy = game.getBufferStrategy();
		while(running) {
			float deltaTime = timer.elapsed();
						
			player.update(deltaTime);
			
			Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
			g2d.clearRect(0, 0, width, height);
			
			player.render(g2d);
			
			g2d.setColor(Color.red);
			g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 20f));
			g2d.drawString(String.valueOf(player.getHealth()), 10, game.getHeight() - 10);
			
			g2d.dispose();
			bufferStrategy.show();
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {}
		}
		bufferStrategy.dispose();
	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
				player.moveUp();
				break;
			case KeyEvent.VK_S:
				player.moveDown();
				break;
			case KeyEvent.VK_A:
				player.moveLeft();
				break;
			case KeyEvent.VK_D:
				player.moveRight();
				break;
			case KeyEvent.VK_SHIFT:
				player.walk(true);
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_S:
				player.stopVerticalMovement();
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_D:
				player.stopHorizontalMovement();
				break;
			case KeyEvent.VK_SHIFT:
				player.walk(false);
				break;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		player.aim(e.getX(), e.getY());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		player.shoot(e.getX(), e.getY());
	}

	@Override
	public void mousePressed(MouseEvent e) {}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}
}