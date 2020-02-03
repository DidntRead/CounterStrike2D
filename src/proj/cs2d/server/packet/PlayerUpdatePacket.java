package proj.cs2d.server.packet;

import java.nio.ByteBuffer;

public class PlayerUpdatePacket extends Packet {
	private int x,y,health;
	
	protected PlayerUpdatePacket(int x, int y, int health) {
		super(PacketType.PLAYER_UPDATE);
		this.x = x;
		this.y = y;
		this.health = health;
	}
	
	public int getHealth() {
		return this.health;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		byte[] data = new byte[13];
		data[0] = (byte)type.ordinal();
		ByteBuffer buf = ByteBuffer.wrap(data, 1, 12);
		buf.putInt(x);
		buf.putInt(y);
		buf.putInt(health);
		return data;
	}
}
