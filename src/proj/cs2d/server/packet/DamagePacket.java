package proj.cs2d.server.packet;

public class DamagePacket extends Packet {
	private int userID, amount;
	
	protected DamagePacket(int userID, int amount) {
		super(PacketType.DAMAGE);
		this.userID = userID;
		this.amount = amount;
	}
	
	public int getUserID() {
		return this.userID;
	}
	
	public int getAmount() {
		return this.amount;
	}
	
	@Override
	public byte[] constructNetworkPacket() {
		return new byte[] {(byte) type.ordinal(), (byte) userID, (byte) amount};
	}
}
