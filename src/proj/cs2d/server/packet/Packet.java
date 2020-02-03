package proj.cs2d.server.packet;

public class Packet {
	protected PacketType type;
	
	protected Packet(PacketType type) {
		this.type = type;
	}
	
	public PacketType getType() {
		return this.type;
	}
	
	public byte[] constructNetworkPacket() {
		return new byte[] {(byte) type.ordinal()};
	}
}
