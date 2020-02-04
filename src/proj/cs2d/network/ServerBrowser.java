package proj.cs2d.network;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import proj.cs2d.Game;

import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.List;
import java.awt.event.ActionEvent;

public class ServerBrowser extends JFrame {
	private JPanel contentPane;
	private JTable table;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					String username = JOptionPane.showInputDialog("Username: ", "Player");
					ServerBrowser frame = new ServerBrowser(username);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public ServerBrowser(String username) {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnConnect = new JButton("Connect");
		btnConnect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SocketAddress addr = (SocketAddress) table.getModel().getValueAt(table.getSelectedRow(), 2);
				connect(addr, username);
			}
		});
		btnConnect.setBounds(335, 227, 89, 23);
		contentPane.add(btnConnect);
		
		JButton btnManual = new JButton("Manual");
		btnManual.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String address = JOptionPane.showInputDialog(contentPane, "Enter server address:port");
				String[] data = address.split(":");
				SocketAddress addr = new InetSocketAddress(data[0], Integer.valueOf(data[1]));
				setVisible(false);
				dispose();
				connect(addr, username);
			}
		});
		btnManual.setBounds(236, 227, 89, 23);
		contentPane.add(btnManual);
		
		JButton btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		btnRefresh.setBounds(137, 227, 89, 23);
		contentPane.add(btnRefresh);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(0, 0, 434, 223);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPane.setViewportView(table);
		
		refresh();
	}
	
	private void refresh() {
		try {
			List<ServerInfo> servers = ServerBroadcaster.getLocalServers();
			table.setModel(new DefaultTableModel() {
				@Override
				public int getColumnCount() {
					return 2;
				};
				
				@Override
				public int getRowCount() {
					return servers.size();
				};
				
				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				};
								
			    @Override
				public String getColumnName(int index) {
			        if(index == 0) return "Name";
			        else return "Players";
			    }
			    
			    @Override
				public Object getValueAt(int row, int column) {
			    	ServerInfo info = servers.get(row);
			    	if(column == 0) {
			    		return info.name;
			    	} else if(column == 1) {
			    		return info.playerCount + "/" + info.maxPlayerCount;
			    	} else {
			    		return info.addr;
			    	}
			    };
			});
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	private void connect(SocketAddress addr, String username) {
		System.out.println(addr.toString());
		Socket sock = new Socket();
		try {
			sock.connect(addr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(!sock.isConnected()) {
			System.out.println("Failed to connect");
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new Game(sock, username).start();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
		return;
	}
}
