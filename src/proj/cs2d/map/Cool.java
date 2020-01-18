package proj.cs2d.map;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.concurrent.ThreadLocalRandom;

public class Cool extends Map {
	public Cool() {
		super(10240);
		
		spawnPoint0 = new MapObject(new Rectangle(200, 200, 100, 100), false);
		spawnPoint1 = new MapObject(new Rectangle(400, 400, 100, 100), false);
		
		//tree.insert(new WoodBox(50, 50, 200, 200));
		for(int i = 0; i < 100; i++) {
			tree.insert(new RenderableMapObject(ThreadLocalRandom.current().nextInt(5000), ThreadLocalRandom.current().nextInt(5000), 100, 50, Color.green));
		}
	}
}
