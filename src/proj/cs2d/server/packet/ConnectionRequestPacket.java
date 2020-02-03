package proj.cs2d.server.packet;

public class ConnectionRequestPacket extends Packet {
	private String username;
	
	protected ConnectionRequestPacket(String username) {
		super(PacketType.CONNECTION_REQUEST);
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		byte[] data = new byte[2 + username.length()];
		data[0] = (byte)type.ordinal();
		data[1] = (byte) username.length();
		for(int i = 0; i < username.length(); i++) {
			data[2 + i] = (byte) username.charAt(i);
		}
		return data;
	}
}
