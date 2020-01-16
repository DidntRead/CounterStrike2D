package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
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
	private int rotation = 45;
	private int hitX = 0, hitY = 0;
	private int health = 100;
	
	/**
	 * Create a new player
	 * @param positionX player's x position
	 * @param positionY player's y position
	 */
	public Player(int positionX, int positionY) {
		this.positionX = positionX;
		this.positionY = positionY;
		if(img == null) {
			try {
				img = ImageIO.read(Player.class.getResourceAsStream("/player.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Move player left
	 */
	public void moveLeft() {
		velocityX = -speed;
	}
	
	/**
	 * Move player right
	 */
	public void moveRight() {
		velocityX = speed;
	}
	
	/**
	 * Move player up
	 */
	public void moveUp() {
		velocityY = -speed;
	}
	
	/**
	 * Move player down
	 */
	public void moveDown() {
		velocityY = speed;
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
	public void update(float delta) {
		positionX += (velocityX * delta);
		positionY += (velocityY * delta);
	}
	
	/**
	 * Aim player at mouse
	 * @param x mouse x position
	 * @param y mouse y position
	 */
	public void aim(int x, int y) {
		double angle1 = Math.atan2(-positionY, -positionX);
		double angle2 = Math.atan2(y - positionY, x - positionX);
		rotation = (int) Math.toDegrees(angle2 - angle1);
	}
	
	/**
	 * Shoot
	 * @param x mouse x position
	 * @param y mouse y position
	 */
	public void shoot(int x, int y) {
		System.out.println("Shoot: " + x + " " + y);
		
		hitX = x;
		hitY = y;
		
		//TODO uceli neshto
	}
	
	/**
	 * Render player
	 * @param g graphics object
	 */
	public void render(Graphics2D g) {
		AffineTransform transf = g.getTransform();
		g.rotate(Math.toRadians(rotation), positionX, positionY);
		g.drawImage(img, positionX - 17, positionY - 14, positionX + 18, positionY + 15, 0, 0, 35, 29, null);
		g.setTransform(transf);
		
		// Shoot line
		if(hitX != 0) {
			g.setColor(Color.orange);
			g.drawLine(positionX, positionY, hitX, hitY);
			hitX = 0;
		}
	}
}
