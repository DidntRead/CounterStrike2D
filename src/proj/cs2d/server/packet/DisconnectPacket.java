package proj.cs2d.server.packet;

public class DisconnectPacket extends Packet {
	private int userID;
	
	protected DisconnectPacket(int userID) {
		super(PacketType.DISCONNECT);
		this.userID = userID;
	}
	
	public int getUserID() {
		return this.userID;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		return new byte[] {(byte) type.ordinal(), (byte) userID};
	}
}
