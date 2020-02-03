package proj.cs2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import proj.cs2d.map.Map;
import proj.cs2d.map.MapObject;
import proj.cs2d.map.RemotePlayer;
import proj.cs2d.server.packet.PacketFactory;

public class Player {
	private static Image[] images = null;
	private static final int MOVE_SPEED = 150;
	private static final int SNEAK_SPEED = 75;
	
	// Movement
	private Rectangle bounds;
	public int velocityX, velocityY;
	private int speed = 140;
	private double rotation = 0;
	
	// Damage & health
	private boolean alive = false;
	private int hitX = Integer.MIN_VALUE, hitY;
	private int hitFrames = 0;
	private int health = -1;
	private int damage = 25;
	private Cooldown shoot;
	private float damageDropoff = 0.8f;
	private int dropoffDistance = 100;
	private boolean heldDown = false;
	
	// Bullets
	private boolean reloading = false;
	private int clipSize = 30;
	private int bullets = clipSize;
	private Cooldown reloadCooldown;
	
	private int team = 0;
	private Image img;
	
	public Player(Map map, int team) {
		this.velocityX = 0;
		this.velocityY = 0;
		this.team = team;
		this.shoot = new Cooldown(175);
		this.reloadCooldown = new Cooldown(1500);
		Point p = map.getSpawnPosition(team);
		this.img = getImage(team);
		this.bounds = new Rectangle(p.x, p.y, img.getWidth(null), img.getHeight(null));
	}
	
	public void render(Graphics2D g2d) {
		if(alive) {
			AffineTransform trans = g2d.getTransform();
			g2d.rotate(rotation, bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2));
			g2d.drawImage(img, bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, 0, 0, bounds.width, bounds.height, null);
			g2d.setTransform(trans);
			g2d.setColor(Color.ORANGE);
			if(hitX != Integer.MIN_VALUE) {
				hitFrames++;
				g2d.drawLine(hitX, hitY, bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2));
				if(hitFrames == 3) {
					hitX = Integer.MIN_VALUE;
					hitFrames = 0;
				}
			}
		}
	}
	
	public double getRotation() {
		return this.rotation;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public boolean isAlive() {
		return this.alive;
	}
	
	public int getTeam(int team) {
		return this.team;
	}
	
	public Rectangle getBounds() {
		return this.bounds;
	}
	
	public void aim(int x, int y, Camera camera) {
		if(alive) {
			int playerX = (bounds.x + bounds.width / 2) + camera.getX();
			int playerY = (bounds.y + bounds.height / 2) + camera.getY();
			int topLeftX = playerX - (camera.getWidth() / 2);
			int topLeftY = playerY - (camera.getHeight() / 2);
			double angle1 = Math.atan2(topLeftY - playerY, topLeftX - playerX);
			double angle2 = Math.atan2(y - playerY, x - playerX);
			rotation = angle2 - angle1;
		}
	}
	
	public void update(double delta, Camera camera, Map map, OutputStream out) {
		int changeX = (int) (velocityX * delta);
		int changeY = (int) (velocityY * delta);
		
		if(alive) {
			if(!map.collide(new Rectangle(bounds.x + changeX, bounds.y, bounds.width, bounds.height), this)) {
				bounds.x += changeX;
				camera.update(-changeX, 0);
			}
		
			// Y
			if(!map.collide(new Rectangle(bounds.x, bounds.y + changeY, bounds.width, bounds.height), this)) {
				bounds.y += changeY;
				camera.update(0, -changeY);
			}
		
			try {
				out.write(PacketFactory.createPlayerUpdatePacket(bounds.x, bounds.y, health).constructNetworkPacket());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			if(Game.enableViewRectangle > 0) {
				camera.updateViewPolygon(this, map);
			}
		
			if(heldDown) {
				shoot(map, out);
			}
		
			if(reloadCooldown.hasPassed()) {
				reloading = false;
			}
		} else {
			bounds.x += changeX;
			camera.update(-changeX, 0);
			
			bounds.y += changeY;
			camera.update(0, -changeY);
		}
	}
	
	public void reload() {
		reloadCooldown.reset();
		bullets = clipSize;
		reloading = true;
	}
	
	public boolean isReloading() {
		return this.reloading;
	}
	
	public float reloadRemaining() {
		return this.reloadCooldown.remaining();
	}
	
	public int getAmmoLeft() {
		return this.bullets;
	}
	
	public int getClipSize() {
		return this.clipSize;
	}
	
	public int getHealth() {
		return this.health;
	}
	
	/**
	 * Increase or decrease player health
	 * @param v amount to increase or decrease
	 */
	public void changeHealth(int v) {
		this.health += v;
	}
	
	public void sneak(boolean v) {
		this.speed = v ? SNEAK_SPEED : MOVE_SPEED;
	}
	
	public int getX() {
		return this.bounds.x;
	}
	
	public int getY() {
		return this.bounds.y;
	}
	
	public int getCenterX() {
		return this.bounds.x + this.bounds.width / 2 ;
	}
	
	public int getCenterY() {
		return this.bounds.y + this.bounds.height / 2 ;
	}
	
	public int getWidth() {
		return this.bounds.width;
	}
	
	public int getHeight() {
		return this.bounds.height;
	}
	
	public void mouseHeldDown(boolean v) {
		this.heldDown = v;
	}
	
	public static Image getImage(int team) {
		if(images == null) {
			images = new Image[2];
			try {
				images[0] = ImageIO.read(Player.class.getResourceAsStream("/player0.png"));
				images[1] = ImageIO.read(Player.class.getResourceAsStream("/player1.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return images[team];
	}

	public void shoot(Map map, OutputStream out) {
		if(shoot.hasPassed() && !reloading && bullets > 0 && alive) {
			shoot.reset();
			bullets--;
			Raycast raycast = new Raycast(bounds.x + (bounds.width / 2), bounds.y + (bounds.height / 2), rotation);
			while(raycast.getLength() < 900) {
				Point p = raycast.progress();
				MapObject obj = map.collide(p);
				hitX = p.x;
				hitY = p.y;
				if(obj != null) {
					if(obj instanceof RemotePlayer) {
						RemotePlayer other = (RemotePlayer)obj;
						if(other.getTeam() != this.team) {
							System.out.println((int) (this.damage * (Math.pow(damageDropoff, raycast.getLength() / dropoffDistance))));
							int damage = (int) (this.damage * (Math.pow(damageDropoff, raycast.getLength() / dropoffDistance)));
							((RemotePlayer)obj).damage(damage);
							try {
								out.write(PacketFactory.createDamagePacket(((RemotePlayer)obj).getUserID(), damage).constructNetworkPacket());
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
					break;
				}
			}
			try {
				out.write(PacketFactory.createShootPacket(bounds.x, bounds.y, hitX, hitY).constructNetworkPacket());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if(bullets == 0) {
			reload();
		}
	}

	public void setPosition(int x, int y, Camera camera) {
		this.bounds.x = x;
		this.bounds.y = y;
		camera.absoluteUpdate(-(this.bounds.x - camera.getWidth() / 2), -(this.bounds.y - camera.getHeight() / 2));
	}
	
	public void setAlive(boolean b) {
		this.alive = b;
		this.bullets = this.clipSize;
		this.health = 100;
	}
}
