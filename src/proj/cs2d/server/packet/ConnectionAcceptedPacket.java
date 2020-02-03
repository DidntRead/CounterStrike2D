package proj.cs2d.server.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;

import proj.cs2d.map.Map;

public class ConnectionAcceptedPacket extends Packet {
	private int team;
	private int userID;
	private Map map;
	
	protected ConnectionAcceptedPacket(int userID, int team, Map map) {
		super(PacketType.CONNECTION_ACCEPTED);
		this.team = team;
		this.userID = userID;
		this.map = map;
	}
	
	public int getTeam() {
		return this.team;
	}
	
	public Map getMap() {
		return this.map;
	}
	
	public int getUserID() {
		return this.userID;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ObjectOutputStream objOut;
		try {
			objOut = new ObjectOutputStream(out);
			objOut.writeObject(map);
			objOut.flush();
			objOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		byte[] mapData = out.toByteArray();
		byte[] data = new byte[7 + mapData.length];
		data[0] = (byte)type.ordinal();
		data[1] = (byte)userID;
		data[2] = (byte)team;
		ByteBuffer.wrap(data, 3, 4).putInt(mapData.length);
		System.arraycopy(mapData, 0, data, 7, mapData.length);
		return data;
	}
}
