package proj.cs2d.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class ServerBroadcaster {
	private int playerCount;
	private InetSocketAddress groupAddress;
	private MulticastSocket socket;
	private Thread thread;
	
	public ServerBroadcaster(String name, int maxPlayerCount) throws IOException {
		this.groupAddress = new InetSocketAddress("224.0.171.0", 5000);
		this.socket = new MulticastSocket(groupAddress.getPort());
		this.socket.joinGroup(groupAddress.getAddress());
		this.socket.setSoTimeout(10000);
		this.thread = new Thread(new Runnable() {
			@Override
			public void run() {
				byte[] buf = new byte[6];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				while(!thread.isInterrupted()) {
					try {
						socket.receive(packet);
						if(Arrays.equals(packet.getData(), "SERVER".getBytes())) {
							byte[] data = new byte[1 + name.length() + 1 + 1];
							data[0] = (byte)name.length();
							for(int i = 0; i < name.length(); i++) {
								data[i + 1] = (byte) name.charAt(i);
							}
							data[data.length - 2] = (byte) playerCount;
							data[data.length - 1] = (byte) maxPlayerCount;
							socket.send(new DatagramPacket(data, data.length, groupAddress));
						}
					} catch (IOException e) {}
				}
			}
		}, "ServerBroadcaster");
	}
	
	public void setPlayerCount(int v) {
		this.playerCount = v;
	}
	
	public void startBroadcast() {
		this.thread.start();
	}
	
	public void stopBroadcast() {
		this.thread.interrupt();
	}
	
	public static List<ServerInfo> getLocalServers() throws IOException {
		HashSet<ServerInfo> list = new HashSet<ServerInfo>();
		
		InetSocketAddress addr = new InetSocketAddress("224.0.171.0", 5000);
		MulticastSocket sock = new MulticastSocket(addr.getPort());
		sock.joinGroup(addr.getAddress());
		sock.setSoTimeout(1000);
		DatagramPacket requestPacket = new DatagramPacket("SERVER".getBytes(), 6, addr);
		byte[] data = new byte[128];
		DatagramPacket receivePacket = new DatagramPacket(data, data.length);
		sock.send(requestPacket);
		while(true) {
			try {
				sock.receive(receivePacket);
			} catch (SocketTimeoutException e) {
				break;
			}
			processServerPacket(receivePacket, list);
		}
		sock.close();
		return new ArrayList<ServerInfo>(list);
	}
	
	private static void processServerPacket(DatagramPacket receivePacket, HashSet<ServerInfo> list) {
		ServerInfo info = new ServerInfo();
		byte[] data = receivePacket.getData();
		byte nameLength = data[0];
		if(nameLength == 83) {
			return;
		}
		info.name = new String(data, 1, nameLength + 1);
		info.playerCount = data[nameLength + 1];
		info.maxPlayerCount = data[nameLength + 2];
		info.addr = receivePacket.getSocketAddress();
		list.add(info);
	}
}
