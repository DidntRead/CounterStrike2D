package proj.cs2d;

import proj.cs2d.map.MapLoader;

public class Main {
	public static void main(String[] args) {
		new Game(MapLoader.load(Main.class.getResourceAsStream("/cool.map"))).start();
	}
}
