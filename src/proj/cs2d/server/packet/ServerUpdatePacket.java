package proj.cs2d.server.packet;

import java.nio.ByteBuffer;

public class ServerUpdatePacket extends Packet {
	private EnemyInfo[] info;
	
	protected ServerUpdatePacket(EnemyInfo[] info) {
		super(PacketType.SERVER_UPDATE);
		this.info = info;
	}
	
	public EnemyInfo[] getInfo() {
		return this.info;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		byte[] data = new byte[2 + info.length * 14];
		data[0] = (byte)type.ordinal();
		data[1] = (byte)info.length;
		for(int i = 0; i < info.length; i++) {
			data[(i * 14) + 2] = (byte) info[i].userID;
			data[(i * 14) + 3] = (byte) info[i].team;
			ByteBuffer buf = ByteBuffer.wrap(data, (i * 14) + 4, 12);
			buf.putInt(info[i].x);
			buf.putInt(info[i].y);
			buf.putInt(info[i].health);
		}
		return data;
	}
}
