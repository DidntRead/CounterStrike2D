package proj.cs2d.map.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.beans.PropertyChangeListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.security.KeyStore;

import javax.swing.border.EmptyBorder;

import proj.cs2d.Game;
import proj.cs2d.map.Cool;
import proj.cs2d.map.HealthPickup;
import proj.cs2d.map.Map;
import proj.cs2d.map.WoodBox;

public class Editor extends JFrame {
	private static Editor frame;
	private static MapPanel panel;
	static int chosen = 0;
	static Color chosenColor = Color.blue;
	static int restoreAmount = 50;
	static int cooldown = 5;
	static int chosenTeam = 0;
	static int align = 16;
	static boolean collidable = true;
	private JPanel contentPane;
	static Map map = new Cool();
	private JTextField textField;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new Editor();
					frame.setVisible(true);
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(WindowEvent e) {
							frame.setVisible(false);
							frame.dispose();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public Editor() {
		setTitle("MapEditor");
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton btnNewButton = new JButton("Run");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int team = JOptionPane.showOptionDialog(contentPane, "Choose spawn team", "Run", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Team 0", "Team 1"}, "Team 0");
				System.out.println("Team: " + team);
				Thread thread = new Thread(new Runnable() {	
					@Override
					public void run() {
						Game game = new Game(map);
						game.start();
						setVisible(true);
					}
				});
				thread.start();
				setVisible(false);
			}
		});
		btnNewButton.setBounds(287, 9, 89, 23);
		contentPane.add(btnNewButton);

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = textField.getText();
				if(Character.isDigit(e.getKeyChar())) text += e.getKeyChar();
				align = Integer.valueOf(text);
			}
		});
		textField.setText("16");
		textField.setBounds(488, 12, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Snap to");
		lblNewLabel.setBounds(440, 15, 46, 14);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(20, 45, 564, 516);
		contentPane.add(scrollPane);
		
		JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		
		horizontal.setUnitIncrement(64);
		vertical.setUnitIncrement(64);
		horizontal.setBlockIncrement(64);
		vertical.setBlockIncrement(64);
		
		panel = new MapPanel();
		scrollPane.setViewportView(panel);
						
		horizontal.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				panel.scrollHorizontal(e.getValue());
				repaint();
			}
		});
		vertical.addAdjustmentListener(new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) {
				panel.scrollVertical(e.getValue());
				repaint();
			}
		});
				
		JButton btnNew = new JButton("New");
		btnNew.setBounds(10, 9, 89, 23);
		btnNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String mapSize = JOptionPane.showInputDialog("Input map size: ");
				int size = mapSize == null ? 10240 : Integer.valueOf(mapSize);
				map = new Map(size);
				repaint();
			}
		});
		contentPane.add(btnNew);
		
		JButton btnLoad = new JButton("Load");
		btnLoad.setBounds(103, 9, 89, 23);
		btnLoad.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showOpenDialog(contentPane);
				if(fileChooser.getSelectedFile() == null) return;
				try {
					ObjectInputStream inp = new ObjectInputStream(new FileInputStream(fileChooser.getSelectedFile()));
					map = (Map)inp.readObject();
					inp.close();
					repaint();
				} catch (IOException | ClassNotFoundException e1) {
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(btnLoad);
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(195, 9, 89, 23);
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.showSaveDialog(contentPane);
				if(fileChooser.getSelectedFile() == null) return;
				try {
					ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileChooser.getSelectedFile()));
					out.writeObject(map);
					out.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		contentPane.add(btnSave);
		
		JPanel toolbar = new JPanel();
		toolbar.setBounds(594, 45, 190, 516);
		contentPane.add(toolbar);
		toolbar.setLayout(new GridLayout(5, 2, 0, 0));
		
		createToolbar(toolbar);
		
		JCheckBox chckbxNewCheckBox = new JCheckBox("Collidable");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.setBounds(580, 9, 97, 23);
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				collidable = chckbxNewCheckBox.isSelected();
			}
		});
		contentPane.add(chckbxNewCheckBox);
	}
	
	private void createToolbar(JPanel toolbar) {
		JPanel[] squares = new JPanel[10];
		for(int i = 0; i < squares.length; i++) {
			squares[i] = new JPanel();
			squares[i].setSize(95, 103);
			toolbar.add(squares[i]);
		}
		
		// Move
		try {
			JButton move = new JButton(new ImageIcon(ImageIO.read(Editor.class.getResourceAsStream("/move.png")).getScaledInstance(95, 103, Image.SCALE_DEFAULT)));
			move.setBackground(Color.white);
			move.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					chosen = 0;
				}
			});
			squares[0].add(move);
		} catch (IOException e1) {
			
		}
		
		// Colored rect
		BufferedImage img = new BufferedImage(95, 103, BufferedImage.TYPE_INT_RGB);
		Graphics color = img.createGraphics();
		color.setColor(chosenColor);
		color.fillRect(0, 0, 95, 103);
		JButton colorRect = new JButton(new ImageIcon(img));
		colorRect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chosen = 1;
			}
		});
		colorRect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					Color newColor = JColorChooser.showDialog(contentPane, "Choose rectangle color", chosenColor);
					color.setColor(newColor);
					color.fillRect(0, 0, 95, 103);
					chosenColor = newColor;
				}
			}
		});
		squares[1].add(colorRect);
		
		// Wood box
		JButton woodBox = new JButton(new ImageIcon(WoodBox.getImage().getScaledInstance(95, 103, Image.SCALE_DEFAULT)));
		woodBox.setBackground(Color.white);
		woodBox.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				chosen = 2;
			}
		});
		squares[2].add(woodBox);
		
		// Health pickup
		JButton healthPickup = new JButton(new ImageIcon(HealthPickup.getImage().getScaledInstance(95, 103, Image.SCALE_DEFAULT)));
		healthPickup.setBackground(Color.white);
		healthPickup.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chosen = 3;
			}
		});
		healthPickup.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					restoreAmount = Integer.valueOf(JOptionPane.showInputDialog(contentPane, "Amount to restore: ", restoreAmount));
					cooldown = Integer.valueOf(JOptionPane.showInputDialog(contentPane, "Respawn cooldown in seconds: ", cooldown));
				}
			}
		});
		squares[3].add(healthPickup);
		
		// Spawn point
		JButton spawnPoint = new JButton("<html>Spawn point<br>" + (chosenTeam == 0 ? "Team0" : "Team1") + "</html>");
		spawnPoint.setBackground(Color.white);
		spawnPoint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chosen = 4;
			}
		});
		spawnPoint.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					chosenTeam = JOptionPane.showOptionDialog(contentPane, "Choose team for spawn point", "Run", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Team 0", "Team 1"}, "Team 0");
					spawnPoint.setText("<html>Spawn point<br>" + (chosenTeam == 0 ? "Team0" : "Team1") + "</html>");
				}
			}
		});
		squares[4].add(spawnPoint);
	}
}
