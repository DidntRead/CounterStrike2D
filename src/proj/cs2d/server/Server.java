package proj.cs2d.server;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import proj.cs2d.map.Map;
import proj.cs2d.network.ServerBroadcaster;
import proj.cs2d.server.packet.EnemyInfo;
import proj.cs2d.server.packet.PacketFactory;
import proj.cs2d.server.packet.ServerUpdatePacket;

public class Server {
	private ServerSocket socket;
	private Thread consoleThread;
	private ServerBroadcaster broadcaster;
	private Hashtable<Integer, ClientConnection> clients;
	private List<Integer> freeIDs;
	private Map map;
	private volatile boolean run = true;
	
	// Configuration
	private boolean localServer = false;
	private boolean advertiseLan = true;
	private int maxPlayerCount;
	
	private int playerCount = 0;
	
	public Server(String name, Map map, int maxPlayerCount, int port, boolean advertiseLan, boolean local) throws Exception {		
		this.map = map;
		this.maxPlayerCount = maxPlayerCount;
		this.advertiseLan = advertiseLan;
		this.localServer = local;
		
		this.clients = new Hashtable<Integer, ClientConnection>(maxPlayerCount);
		this.freeIDs = new ArrayList<Integer>(maxPlayerCount);
		for(int i = 0; i <= maxPlayerCount; i++) {
			freeIDs.add(i);
		}
		
		// Socket
		socket = new ServerSocket(port);
		socket.setSoTimeout(2000);
		
		// Console thread
		consoleThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
					while(run) {
						if(reader.ready()) {
							String inp[] = reader.readLine().split(" ");
							String cmd = inp[0];
							switch (cmd) {
							case "exit":
							case "stop":
								run = false;
								break;
							case "disconnect":
							case "dc":
								int id = Integer.valueOf(inp[1]);
								disconnectClient(id);
								break;
							case "count":
								System.out.println("Currently there are " + clients.size() + " connected players");
								break;
							case "kill":
								clients.get(Integer.valueOf(inp[1])).damaged(1000);
								break;
							case "damage":
							case "dmg":
								clients.get(Integer.valueOf(inp[1])).damaged(Integer.valueOf(inp[2]));
								break;
							case "list":
								StringBuilder builder = new StringBuilder("List of clients:\nID USERNAME\n");
								clients.forEach((ID, client) -> {
									builder.append(ID);
									builder.append(' ');
									builder.append(client.getUsername());
									builder.append("\n");
								});
								System.out.println(builder.toString());
								break;
							case "help":
								System.out.println("stop - stop server\ndisconnect <userID> - disconnect player with given userID\ncount - number of players currently connected\nkill <userID> - kill player with given userID\ndamage <userID> <amount> - inflict given amount of damage to player with given userID\nlist - list all players and their userIDs");
								break;
							default:
								System.err.println("Unknown command");
							}
						}
					}
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}}, "consoleThread");
		
		// ServerBroadcaster
		if(advertiseLan) {
			broadcaster = new ServerBroadcaster(name, maxPlayerCount, port);
		}
	}
	
	public void start() {
		// Start console thread
		consoleThread.start();
		// Start lan advertisement if enabled
		if(advertiseLan) broadcaster.startBroadcast();
		
		System.out.println("Server started");
		
		// Main loop
		while(run) {
			try {
				System.out.println("Checking for respawn");
				
				if(shouldRespawn()) {
					System.out.println("Respawning");
					for(ClientConnection client : clients.values()) {
						Point p = map.getSpawnPosition(client.getTeam());
						client.respawn(p.x, p.y);
					}
				}
				
				Socket connection = socket.accept();
				System.out.println("New connection");
				new ClientConnection(this, connection, map, 15000);
			} catch (Exception ignored) {}
		}
		
		System.out.println("Server stopping");
		
		if(advertiseLan) {
			broadcaster.stopBroadcast();
		}
		
		try {
			consoleThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println("Server stopped");
	}
	
	public void stop() {
		run = false;
		consoleThread.interrupt();
	}
	
	private boolean shouldRespawn() {
		if(localServer) {
			if(clients.size() == 0) return false;
			if(clients.get(0).getHealth() <= 0) return true;
			else return false;
		}
		
		boolean team0Dead = true;
		boolean team1Dead = true;
		
		for(ClientConnection client : clients.values()) {
			if(client.getHealth() > 0) {
				if(client.getTeam() == 0) team0Dead = false;
				else team1Dead = false;
			}
		}
		
		if(team0Dead || team1Dead) {
			System.out.println("Round winner: " + (team0Dead ? "Team 1" : "Team 0"));
			System.out.println("New round");
		}
		
		return team0Dead || team1Dead;
	}
	
	protected boolean hasFreeSlots() {
		return playerCount < maxPlayerCount;
	}
	
	protected int getNewUserID() {
		if(freeIDs.size() > 0) {
			return freeIDs.remove(0);
		}
		return -1;
	}
	
	protected void shot(int userID, int x, int y, int x1, int y1) {
		clients.forEach((id, client) -> {
			client.shot(x, y, x1, y1);
		});
	}
	
	protected int getTeam(int userID) {
		return userID % 2;
	}
	
	protected void damaged(int hitPlayerID, int amount) {
		clients.get(hitPlayerID).damaged(amount);
	}
	
	protected void updateClients() {
		EnemyInfo[] infos = new EnemyInfo[clients.size()];
		Index index = new Index();
		clients.forEach((id, client) -> {
			EnemyInfo info = new EnemyInfo();
			info.userID = client.getUserID();
			info.x = client.getX();
			info.y = client.getY();
			info.team = client.getTeam();
			info.health = client.getHealth();
			infos[index.get()] = info;
			index.increment();
		});
		ServerUpdatePacket packet = (ServerUpdatePacket) PacketFactory.createServerUpdatePacket(infos);
		byte[] data = packet.constructNetworkPacket();
		clients.forEach((id, client) -> {
			client.update(data);
		});
	}
	
	protected void disconnectClient(int id) {
		clients.remove(id).stop();
		freeIDs.add(id);
		System.out.println("Disconnected player: { ID: " + id + " }");
	}
	
	protected void removeClient(int id) {
		freeIDs.add(id);
		clients.remove(id);
	}
	
	public boolean isRunning() {
		return this.run;
	}

	protected void addClient(int id, ClientConnection connection) {
		clients.put(id, connection);
	}
}
