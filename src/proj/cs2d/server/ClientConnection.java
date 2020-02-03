package proj.cs2d.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import proj.cs2d.Cooldown;
import proj.cs2d.map.Map;
import proj.cs2d.server.packet.ConnectionRequestPacket;
import proj.cs2d.server.packet.DamagePacket;
import proj.cs2d.server.packet.Packet;
import proj.cs2d.server.packet.PacketFactory;
import proj.cs2d.server.packet.PlayerUpdatePacket;
import proj.cs2d.server.packet.ShootPacket;

public class ClientConnection {
	private ClientConnection connection;
	private Cooldown timeoutCooldown;
	private InputStream inp;
	private OutputStream out;
	private ScheduledExecutorService executor;

	private String username;
	private int team;
	private int id = -1;
	private int x;
	private int y;
	private int health = -1;
	
	public ClientConnection(Server server, Socket client, Map map, int timeout) throws IOException {
		inp = client.getInputStream();
		out = client.getOutputStream();
		timeoutCooldown = new Cooldown(timeout);
		connection = this;
		executor = Executors.newSingleThreadScheduledExecutor();
		Runnable task = new Runnable() {
			@Override
			public void run() {
				try {
						if(timeoutCooldown.hasPassed()) {
							// Client timeout
							System.out.println("Client timeout { ID: " + id + "}");
							server.removeClient(id);
							executor.shutdownNow();
						}
						int available = inp.available();
						if(available > 0) {
							byte[] data = inp.readNBytes(available);
							Packet packet = PacketFactory.deserializePacket(data);
							switch (packet.getType()) {
							case CONNECTION_REQUEST:
								if(server.hasFreeSlots()) {
									username = ((ConnectionRequestPacket)packet).getUsername();
									id = server.getNewUserID();
									team = server.getTeam(id);
									out.write(PacketFactory.createConnectionAcceptedPacket(id, team, map).constructNetworkPacket());
									server.addClient(id, connection);
									System.out.println("Connection accepted: { ID: " + id + ", Username: " + username + "}");
								} else {
									System.out.println("Connection denied");
									out.write(PacketFactory.createConnectionDeniedPacket().constructNetworkPacket());
								}
								break;
							case DISCONNECT:
								server.removeClient(id);
								System.out.println("Disconnect: { ID: "+ id +"}");
								executor.shutdownNow();
								return;
							case HEARTBEAT:
								timeoutCooldown.reset();
								break;
							case SHOOT:
								ShootPacket shPacket = (ShootPacket)packet;
								server.shot(id, shPacket.getX(), shPacket.getY(), shPacket.getX1(), shPacket.getY1());
								break;
							case DAMAGE:
								DamagePacket dmgPacket = (DamagePacket)packet;
								server.damaged(dmgPacket.getUserID(), dmgPacket.getAmount());
								break;
							case PLAYER_UPDATE:
								PlayerUpdatePacket plUpdate = (PlayerUpdatePacket)packet;
								x = plUpdate.getX();
								y = plUpdate.getY();
								health = plUpdate.getHealth();
								server.updateClients();
								break;
							default:
								System.out.println("Invalid packet received from client: { ID: " + id + "}");
								break;
							}
							out.flush();
						}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}};
			executor.scheduleAtFixedRate(task, 0, 100, TimeUnit.MILLISECONDS);
	}
	
	protected void stop() {
		executor.shutdownNow();
	}
	
	protected int getUserID() {
		return this.id;
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getTeam() {
		return this.team;
	}
	
	public int getHealth() {
		return health;
	}
	
	public String getUsername() {
		return this.username;
	}

	protected void respawn(int spawnX, int spawnY) {
		try {
			out.write(PacketFactory.createRespawnPacket(spawnX, spawnY).constructNetworkPacket());
			out.flush();
		} catch (IOException e) {}
	}
	
	protected void damaged(int amount) {
		try {
			out.write(PacketFactory.createDamagePacket(id, amount).constructNetworkPacket());
			out.flush();
		} catch (IOException e) {}
	}
	
	protected void shot(int x, int y, int x1, int y1) {
		try {
			out.write(PacketFactory.createShootPacket(x, y, x1, y1).constructNetworkPacket());
			out.flush();
		} catch (IOException e) {}
	}

	protected void update(byte[] packet) {
		try {
			out.write(packet);
			out.flush();
		} catch (IOException e) {}
	}
}
