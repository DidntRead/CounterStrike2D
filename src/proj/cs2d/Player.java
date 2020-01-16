package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Player {
	public static final int RUN_SPEED = 120;
	public static final int WALK_SPEED = 40;
	
	private static Image img = null;
	public int speed = RUN_SPEED;
	private int velocityX,velocityY;
	private int positionX,positionY;
	private int playerX, playerY;
	private int rotation = 45;
	private int hitX = 0, hitY = 0;
	private int health = 100;
	private Rectangle aabb;
	
	/**
	 * Create a new player
	 * @param positionX player's x position
	 * @param positionY player's y position
	 */
	public Player(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
		this.playerX = Game.width / 2;
		this.playerY = Game.height / 2;
		if(img == null) {
			try {
				img = ImageIO.read(Player.class.getResourceAsStream("/player.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.aabb = new Rectangle(positionX, positionY, 35, 29);
	}
	
	/**
	 * Move player left
	 */
	public void moveLeft() {
		velocityX = speed;
	}
	
	/**
	 * Move player right
	 */
	public void moveRight() {
		velocityX = -speed;
	}
	
	/**
	 * Move player up
	 */
	public void moveUp() {
		velocityY = speed;
	}
	
	/**
	 * Move player down
	 */
	public void moveDown() {
		velocityY = -speed;
	}
	
	/**
	 * Stop player's vertical movement
	 */
	public void stopVerticalMovement() {
		velocityY = 0;
	}
	
	/**
	 * Stop player's horizontal movement
	 */
	public void stopHorizontalMovement() {
		velocityX = 0;
	}
	
	public Rectangle getBounds() {
		return this.aabb;
	}
	
	/**
	 * Get player's current health
	 * @return health
	 */
	public int getHealth() {
		return this.health;
	}
	
	/**
	 * Make the player walk
	 * @param activate true if player should run or walk otherwise
	 */
	public void walk(boolean activate) {
		if(activate) {
			speed = WALK_SPEED;
		} else {
			speed = RUN_SPEED;
		}
		if(velocityX < 0) velocityX = -speed;
		else if(velocityX > 0) velocityX = speed;
		if(velocityY < 0) velocityY = -speed;
		else if(velocityY > 0) velocityY = speed;
	}
	
	/**
	 * Update player
	 * @param delta time passed since last update
	 */
	public void update(float delta, Map map) {
		if(map.collide(new Rectangle((int) (velocityX * delta) - 70, -58, 35, 29), this)) {
			velocityX = 0;
		} else {
			positionX += (velocityX * delta);
		}
		
		if(map.collide(new Rectangle(-70, (int) (velocityY * delta) -58, 35, 29), this)) {
			velocityY = 0;
		} else {
			positionY += (velocityY * delta);
		}
				
		this.aabb.x = positionX;
		this.aabb.y = positionY;
	}
	
	/**
	 * Aim player at mouse
	 * @param x mouse x position
	 * @param y mouse y position
	 */
	public void aim(int x, int y) {
		double angle1 = Math.atan2(-playerY, -playerX);
		double angle2 = Math.atan2(y - playerY, x - playerX);
		rotation = (int) Math.toDegrees(angle2 - angle1);
	}
	
	/**
	 * Shoot
	 * @param x mouse x position
	 * @param y mouse y position
	 */
	public void shoot(Map map) {
		Raycast raycast = new Raycast(playerX, playerY, rotation);
		while(raycast.getLength() < 850) {
			System.out.println(raycast.getLength());
			Point p = raycast.progress();
			MapObject obj = map.collide(p, this);
			hitX = p.x;
			hitY = p.y;
			if(obj != null) {
				System.out.println("HIT");
				break;
			}
		}
	}
	
	/**
	 * Render player
	 * @param g graphics object
	 */
	public void render(Graphics2D g) {
		AffineTransform transf = g.getTransform();
		g.rotate(Math.toRadians(rotation), playerX, playerY);
		g.drawImage(img, playerX - 17,  playerY - 14,  playerX + 18,  playerY + 15, 0, 0, 35, 29, null);
		g.setTransform(transf);
		
		// Shoot line
		if(hitX != 0) {
			g.setColor(Color.orange);
			g.drawLine(playerX, playerY, hitX, hitY);
			hitX = 0;
		}
	}
}
