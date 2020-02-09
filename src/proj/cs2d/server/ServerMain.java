package proj.cs2d.server;

import proj.cs2d.map.ColoredCool;

public class ServerMain {
	public static void main(String[] args) throws Exception {
		Server server = new Server("Dick", new ColoredCool(), 4, 5000, true, false);
		server.start();
	}
}
