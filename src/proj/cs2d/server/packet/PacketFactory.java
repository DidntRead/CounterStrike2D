package proj.cs2d.server.packet;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.nio.ByteBuffer;

import proj.cs2d.map.Map;

public class PacketFactory {
	public static Packet createConnectionRequestPacket(String username) {
		return new ConnectionRequestPacket(username);
	}
	
	public static Packet createHeartbeatPacket() {
		return new Packet(PacketType.HEARTBEAT);
	}
	
	public static Packet createConnectionDeniedPacket() {
		return new Packet(PacketType.CONNECTION_DENIED);
	}
	
	public static Packet createDisconnectPacket(int userID) {
		return new DisconnectPacket(userID);
	}
	
	public static Packet createPlayerUpdatePacket(int x, int y, int health) {
		return new PlayerUpdatePacket(x, y, health);
	}
	
	public static Packet createRespawnPacket(int spawnX, int spawnY) {
		return new RespawnPacket(spawnX, spawnY);
	}
	
	public static Packet createConnectionAcceptedPacket(int userID, int team, Map map) {
		return new ConnectionAcceptedPacket(userID, team, map);
	}
	
	public static Packet createShootPacket(int x, int y, int x1, int y1) {
		return new ShootPacket(x, y, x1, y1);
	}
	
	public static Packet createDamagePacket(int userID, int amount) {
		return new DamagePacket(userID, amount);
	}
	
	public static Packet createServerUpdatePacket(EnemyInfo[] info) {
		return new ServerUpdatePacket(info);
	}
	
	public static Packet deserializePacket(byte[] data) {
		byte typeInfo = data[0];
		PacketType type = PacketType.values()[typeInfo];
		switch (type) {
		case CONNECTION_DENIED:
		case HEARTBEAT:
			return new Packet(type);
		case CONNECTION_REQUEST:
			int length = data[1];
			String username = new String(data, 2, length);
			return new ConnectionRequestPacket(username);
		case DISCONNECT:
			int userID = data[1];
			return new DisconnectPacket(userID);
		case PLAYER_UPDATE:
			int x = ByteBuffer.wrap(data, 1, 4).getInt();
			int y = ByteBuffer.wrap(data, 5, 4).getInt();
			int health = ByteBuffer.wrap(data, 9, 4).getInt();
			return new PlayerUpdatePacket(x, y, health);
		case RESPAWN:
			x = ByteBuffer.wrap(data, 1, 4).getInt();
			y = ByteBuffer.wrap(data, 5, 4).getInt();
			return new RespawnPacket(x, y);
		case CONNECTION_ACCEPTED:
			userID = data[1];
			byte team = data[2];
			int mapLength = ByteBuffer.wrap(data, 3, 4).getInt();
			ByteArrayInputStream inp = new ByteArrayInputStream(data, 7, mapLength);
			Map map = null;
			try {
				ObjectInputStream objInp = new ObjectInputStream(inp);
				map = (Map)objInp.readObject();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return new ConnectionAcceptedPacket(userID, team, map);
		case SHOOT:
			x = ByteBuffer.wrap(data, 1, 4).getInt();
			y = ByteBuffer.wrap(data, 5, 4).getInt();
			int x1 = ByteBuffer.wrap(data, 1, 4).getInt();
			int y1 = ByteBuffer.wrap(data, 5, 4).getInt();
			return new ShootPacket(x, y, x1, y1);
		case DAMAGE:
			userID = data[1];
			int amount = data[2];
			return new DamagePacket(userID, amount);
		case SERVER_UPDATE:
			length = data[1];
			EnemyInfo[] info = new EnemyInfo[length];
			for(int i = 0; i < length; i++) {
				info[i] = new EnemyInfo();
				info[i].userID = data[(i * 14) + 2];
				info[i].team = data[(i * 14) + 3];
				info[i].x = ByteBuffer.wrap(data, (i * 14) + 4, 4).getInt();
				info[i].y = ByteBuffer.wrap(data, (i * 14) + 8, 4).getInt();
				info[i].health = ByteBuffer.wrap(data, (i * 14) + 12, 4).getInt();
			}
			return new ServerUpdatePacket(info);
		}
		return null;
	}
}
