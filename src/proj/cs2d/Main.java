package proj.cs2d;

import proj.cs2d.map.MapLoader;

public class Main {
	public static void main(String[] args) {
		MapLoader.load(Main.class.getResourceAsStream("/cool.map")).debug();
		new Game(MapLoader.load(Main.class.getResourceAsStream("/cool.map"))).start();
	}
}
