package proj.cs2d.map;

import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;

import proj.cs2d.Cooldown;
import proj.cs2d.Player;

public class HealthPickup extends Pickup implements Updatable {
	private static Image img = null;
	private int healthRestoration;
	private Cooldown cooldown;
	
	public HealthPickup(int x, int y, int width, int height, int healthRestoration, int cooldown) {
		super(x, y, width, height, img == null ? loadImage() : img);
		this.cooldown = new Cooldown(cooldown * 1000);
		this.healthRestoration = healthRestoration;
	}
	
	private static Image loadImage() {
		try {
			HealthPickup.img = ImageIO.read(HealthPickup.class.getResourceAsStream("/health.png"));
		} catch (IOException e) {
		}
		return HealthPickup.img;
	}
	
	public static Image getImage() {
		if(img == null) {
			loadImage();
		}
		return img;
	}

	public int getHealthRestoration() {
		return healthRestoration;
	}
	
	public int getCooldown() {
		return cooldown.getCooldown() / 1000;
	}
	
	public void setHealthRestoration(int v) {
		this.healthRestoration = v;
	}
	
	public void setCooldown(int cooldown) {
		this.cooldown.changeCooldown(cooldown * 1000);
	}
	
	
	@Override
	public void pickedUp(Player player) {
		if(cooldown.hasPassed()) {
			player.changeHealth(healthRestoration);
			super.img = null;
			cooldown.reset();
		}
	}
	
	@Override
	public void update(double delta) {
		if(cooldown.hasPassed()) {
			super.img = HealthPickup.img;
		}
	}
}
