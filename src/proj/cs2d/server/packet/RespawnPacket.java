package proj.cs2d.server.packet;

import java.nio.ByteBuffer;

public class RespawnPacket extends Packet {
	private int spawnX, spawnY;
	
	protected RespawnPacket(int spawnX, int spawnY) {
		super(PacketType.RESPAWN);
		this.spawnX = spawnX;
		this.spawnY = spawnY;
	}

	public int getSpawnX() {
		return spawnX;
	}

	public int getSpawnY() {
		return spawnY;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		byte[] data = new byte[9];
		data[0] = (byte)type.ordinal();
		ByteBuffer buf = ByteBuffer.wrap(data, 1, 8);
		buf.putInt(spawnX);
		buf.putInt(spawnY);
		return data;
	}
}
