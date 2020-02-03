package proj.cs2d.server.packet;

import java.nio.ByteBuffer;

public class ShootPacket extends Packet {
	private int x,y,x1,y1;
	
	protected ShootPacket(int x, int y, int x1, int y1) {
		super(PacketType.SHOOT);
		this.x = x;
		this.y = y;
		this.x1 = x1;
		this.y1 = y1;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getX1() {
		return x1;
	}

	public int getY1() {
		return y1;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		byte[] data = new byte[17];
		data[0] = (byte)type.ordinal();
		ByteBuffer buf = ByteBuffer.wrap(data, 1, 16);
		buf.putInt(x);
		buf.putInt(y);
		buf.putInt(x1);
		buf.putInt(y1);
		return data;
	}
}
