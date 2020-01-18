package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.imageio.ImageIO;

import proj.cs2d.map.Map;
import proj.cs2d.map.MapObject;

public class Player {
	private static Image[] images = null;
	
	public int velocityX, velocityY;
	private int hitX = Integer.MIN_VALUE, hitY;
	private double rotation = 0;
	private int health = 100;
	private Rectangle bounds;
	private int team = 0;
	private Image img;
	private int speed = 120;
	
	public Player(Map map, int team) {
		this.velocityX = 0;
		this.velocityY = 0;
		this.team = team;
		
		if(images == null) {
			images = new Image[2];
			try {
				images[0] = ImageIO.read(Player.class.getResourceAsStream("/player0.png"));
				images[1] = ImageIO.read(Player.class.getResourceAsStream("/player1.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Point p = map.getSpawnPosition(team);
		this.img = images[team];
		this.bounds = new Rectangle(p.x, p.y, img.getWidth(null), img.getHeight(null));
	}
	
	public void render(Graphics2D g2d) {
		AffineTransform trans = g2d.getTransform();
		g2d.rotate(rotation, bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2));
		g2d.drawImage(img, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0, 0, bounds.width, bounds.height, null);
		g2d.setTransform(trans);
		g2d.setColor(Color.ORANGE);
		if(hitX != Integer.MIN_VALUE) {
			g2d.drawLine(hitX, hitY, bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2));
			hitX = Integer.MIN_VALUE;
		}
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public int getTeam(int team) {
		return this.team;
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	public void aim(int x, int y, Camera camera) {
		int playerX = (bounds.x + bounds.width / 2) + camera.getX();
		int playerY = (bounds.y + bounds.height / 2) + camera.getY();
		int topLeftX = playerX - (camera.getWidth() / 2);
		int topLeftY = playerY - (camera.getHeight() / 2);
		double angle1 = Math.atan2(topLeftY - playerY, topLeftX - playerX);
		double angle2 = Math.atan2(y - playerY, x - playerX);
		rotation = angle2 - angle1;
	}
	
	public void update(float delta, Camera camera, Map map) {
		int changeX = (int) (velocityX * delta);
		int changeY = (int) (velocityY * delta);
		
		if(!map.collide(new Rectangle(bounds.x + changeX, bounds.y, bounds.width, bounds.height), this)) {
			bounds.x += changeX;
			camera.update(-changeX, 0);
		}
		
		// Y
		if(!map.collide(new Rectangle(bounds.x, bounds.y + changeY, bounds.width, bounds.height), this)) {
			bounds.y += changeY;
			camera.update(0, -changeY);
		}
	}
	
	public int getHealth() {
		return this.health;
	}
	
	/**
	 * Increase or decrease player health
	 * @param v amount to increase or decrease
	 */
	public void changeHealth(int v) {
		
	}
	
	public int getX() {
		return this.bounds.x;
	}
	
	public int getY() {
		return this.bounds.y;
	}
	
	public int getWidth() {
		return this.bounds.width;
	}
	
	public int getHeight() {
		return this.bounds.height;
	}

	public void shoot(Map map) {
		Raycast raycast = new Raycast(bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2), rotation);
		while(raycast.getLength() < 900) {
			Point p = raycast.progress();
			MapObject obj = map.collide(p);
			hitX = p.x;
			hitY = p.y;
			if(obj != null) {
				System.out.println("Hit");
				break;
			}
		}
	}
}
