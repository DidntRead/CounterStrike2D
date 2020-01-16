package proj.cs2d.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import proj.cs2d.Player;

public class Server implements Runnable{

	
	static ObjectOutputStream[] out=new ObjectOutputStream[100];
	Player[] enemy=new Player[100];
	
	public static void main(String[] args) throws IOException {
		ServerSocket ss=new ServerSocket(99);
		for (int i = 0; i < 100; i++) {
			Socket s=ss.accept();
			new Server(s,i);
			out[i]= new ObjectOutputStream(s.getOutputStream());
		}
	}
	
	Socket s;
	int i;
	
	public Server(Socket s, int i) throws IOException {
		
		this.i=i;
		this.s=s;
		new Thread(this).start();
		
	}



	@Override
	public void run() {
		try {
			ObjectInputStream in=new ObjectInputStream(s.getInputStream());
			while(true){
				enemy[i]=(Player) in.readObject();
				for (int i = 0; i < out.length; i++) {
					if (i!=this.i&&out[i]!=null) {
						out[i].writeObject(enemy);
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}