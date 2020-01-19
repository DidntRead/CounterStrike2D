package proj.cs2d.map;

import java.awt.Color;
import java.awt.Graphics2D;

import proj.cs2d.Player;

public class RemotePlayer extends RenderableMapObject {
	private int team;
	private int health = 100;
	
	public RemotePlayer(int x, int y, int team) {
		super(x, y, 35, 29, Player.getImage(team));
		this.team = team;
	}
	
	@Override
	public void render(Graphics2D g2d) {
		super.render(g2d);
		g2d.setColor(Color.red);
		g2d.fillRect(bounds.x, bounds.y - 10, bounds.width, 5);
		g2d.setColor(Color.green);
		g2d.fillRect(bounds.x, bounds.y - 10, (int)(bounds.width * (health / 100f)), 5);
	}
	
	public void damage(int amount) {
		health -= amount;
		//TODO
		System.out.println("Hit other player");
		if(health <= 0) {
			System.out.println("Killed other player");
		}
	}
	
	public int getTeam() {
		return this.team;
	}
}
