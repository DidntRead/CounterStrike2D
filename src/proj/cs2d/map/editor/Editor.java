package proj.cs2d.map.editor;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.*;
import java.util.List;

import javax.swing.border.EmptyBorder;

import proj.cs2d.Game;
import proj.cs2d.map.ColoredCool;
import proj.cs2d.map.HealthPickup;
import proj.cs2d.map.Map;
import proj.cs2d.map.MapObject;
import proj.cs2d.map.RenderableMapObject;
import proj.cs2d.map.WoodBox;
import proj.cs2d.map.editor.command.Change;
import proj.cs2d.map.editor.command.ChangeCommand;
import proj.cs2d.map.editor.command.MultiChangeCommand;

import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;

public class Editor extends JFrame {
	private static Editor frame;
	private MapPanel panel;
	private JPanel editPanel;
	private int chosen = 0;
	private Color chosenColor = Color.blue;
	private int restoreAmount = 50;
	private int cooldown = 5;
	private int chosenTeam = 0;
	private int align = 2;
	private MapObject displayedEdit;
	private JPanel contentPane;
	private Map map = new ColoredCool();
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
		setBounds(100, 100, 1200, 700);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String mapSize = JOptionPane.showInputDialog("Input map size: ");
				if(mapSize != null) {
					map = new Map(Integer.valueOf(mapSize));
					repaint();
				}
			}
		});
		mnNewMenu.add(mntmNew);
		
		JMenuItem mntmLoad = new JMenuItem("Load");
		mntmLoad.addActionListener(new ActionListener() {
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
		mnNewMenu.add(mntmLoad);
		
		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
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
					e1.printStackTrace();
				}
			}
		});
		mnNewMenu.add(mntmSave);
		
		JMenuItem mntmExport = new JMenuItem("Export");
		mntmExport.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JTextArea area = new JTextArea(30, 60);
				area.setText(map.exportString());
				area.setEditable(false);
				JScrollPane pane = new JScrollPane(area);
				JOptionPane.showMessageDialog(contentPane, pane, "Exported map", JOptionPane.PLAIN_MESSAGE);
			}
		});
		mnNewMenu.add(mntmExport);
		
		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		mnNewMenu.add(mntmExit);
		
		JMenu mnNewMenu_1 = new JMenu("Edit");
		menuBar.add(mnNewMenu_1);
		
		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.undo();
				if(displayedEdit != null) {
					displayEdit(displayedEdit);
				}
				repaint();
			}
		});
		mnNewMenu_1.add(mntmUndo);
		
		JMenuItem mntmRedo = new JMenuItem("Redo");
		mntmRedo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.redo();
				if(displayedEdit != null) {
					displayEdit(displayedEdit);
				}
				repaint();
			}
		});
		mnNewMenu_1.add(mntmRedo);
		
		mnNewMenu_1.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}
			public void menuDeselected(MenuEvent e) {
			}
			public void menuSelected(MenuEvent e) {
				mntmUndo.setEnabled(panel.canUndo());
				mntmRedo.setEnabled(panel.canRedo());
			}
		});

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		// Undo & Redo
		String undoKey = "Undo action";
		String redoKey = "Redo action";
		contentPane.getActionMap().put(undoKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.undo();
				if(displayedEdit != null) {
					displayEdit(displayedEdit);
				}
				repaint();
			}
		});
		contentPane.getActionMap().put(redoKey, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.redo();
				if(displayedEdit != null) {
					displayEdit(displayedEdit);
				}
				repaint();
			}
		});
		
	    InputMap[] inputMaps = new InputMap[] {
	            contentPane.getInputMap(JComponent.WHEN_FOCUSED),
	            contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT),
	            contentPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW),
	    };
	    
	    for(InputMap i : inputMaps) {
	        i.put(KeyStroke.getKeyStroke("control Z"), undoKey);
	        i.put(KeyStroke.getKeyStroke("control Y"), redoKey);
	    }
		
		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int team = JOptionPane.showOptionDialog(contentPane, "Choose spawn team", "Run", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Team 0", "Team 1"}, "Team 0");
				Thread thread = new Thread(new Runnable() {	
					@Override
					public void run() {
						Game game = new Game(map, team);
						game.start();
						setVisible(true);
					}
				});
				thread.start();
				setVisible(false);
			}
		});
		btnRun.setBounds(10, 11, 89, 23);
		contentPane.add(btnRun);
		
		

		textField = new JTextField();
		textField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String text = textField.getText();
				if(Character.isDigit(e.getKeyChar())) text += e.getKeyChar();
				if(text.length() > 0) {
					align = Integer.valueOf(text);
				}
			}
		});
		textField.setText(String.valueOf(align));
		textField.setBounds(160, 11, 86, 20);
		contentPane.add(textField);
		textField.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("Snap to");
		lblNewLabel.setBounds(112, 14, 46, 14);
		contentPane.add(lblNewLabel);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 45, 984, 616);
		contentPane.add(scrollPane);
		
		JScrollBar horizontal = scrollPane.getHorizontalScrollBar();
		JScrollBar vertical = scrollPane.getVerticalScrollBar();
		
		horizontal.setUnitIncrement(64);
		vertical.setUnitIncrement(64);
		horizontal.setBlockIncrement(64);
		vertical.setBlockIncrement(64);
		
		panel = new MapPanel(this);
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
		
		JPanel toolbar = new JPanel();
		toolbar.setBounds(994, 45, 190, 616);
		contentPane.add(toolbar);
		toolbar.setLayout(new GridLayout(6, 2, 0, 0));
		
		createToolbar(toolbar);
		
		editPanel = new JPanel();
		editPanel.setBounds(256, 7, 728, 27);
		contentPane.add(editPanel);
		editPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
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
					repaint();
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
				repaint();
			}
		});
		colorRect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					chosenColor = JColorChooser.showDialog(contentPane, "Choose color", chosenColor);
					if(chosenColor == null) chosenColor = Color.blue;
					color.setColor(chosenColor);
					color.fillRect(0, 0, 95, 103);
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
				repaint();
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
				repaint();
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
				repaint();
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
	
	public void displayEdit(MapObject obj) {
		displayedEdit = obj;
		editPanel.removeAll();
		if(obj == null) {
			repaint();
			return;
		}
		JCheckBox collidable = new JCheckBox("Collidable");
		collidable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.getCommandManager().execute(new ChangeCommand(obj, Change.Collidable, collidable.isSelected()));
			}
		});
		collidable.setSelected(obj.isCollidable());
		editPanel.add(collidable);
		
		if(obj instanceof RenderableMapObject) {
			RenderableMapObject renObj = (RenderableMapObject)obj;
			if(renObj.getColor() != null) {
				JButton color = new JButton("Color");
				color.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						Color newColor = JColorChooser.showDialog(contentPane, "Choose new color", renObj.getColor());
						if(newColor != null) {
							color.setBackground(newColor);
							panel.getCommandManager().execute(new ChangeCommand(obj, Change.Color, newColor));
							repaint();
						}
					}
				});
				color.setBackground(renObj.getColor());
				editPanel.add(color);
			}
		}
		
		if(obj instanceof HealthPickup) {
			HealthPickup health = (HealthPickup) obj;

			JLabel lblHealthRes = new JLabel("Health restoration: ");
			JTextField healthRes = new JTextField(String.valueOf(health.getHealthRestoration()));
			healthRes.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					String value = healthRes.getText() + (Character.isDigit(e.getKeyChar()) ? e.getKeyChar() : "");
					if(value.length() > 0) {
						panel.getCommandManager().execute(new ChangeCommand(obj, Change.HealthRestore, Integer.valueOf(value)));
					}
				}
			});
			editPanel.add(lblHealthRes);
			editPanel.add(healthRes);
			
			JLabel lblCooldown = new JLabel("Cooldown: ");
			JTextField cooldown = new JTextField(String.valueOf(health.getCooldown()));
			cooldown.addKeyListener(new KeyAdapter() {
				@Override
				public void keyTyped(KeyEvent e) {
					String value = cooldown.getText() + (Character.isDigit(e.getKeyChar()) ? e.getKeyChar() : "");
					if(value.length() > 0) {
						panel.getCommandManager().execute(new ChangeCommand(obj, Change.Cooldown, Integer.valueOf(value)));
					}
				}
			});
			editPanel.add(lblCooldown);
			editPanel.add(cooldown);
		}
		
		revalidate();
		repaint();
	}
	
	public void displayEdit(List<MapObject> objects) {		
		editPanel.removeAll();
		
		if(objects == null || objects.size() == 0) return;
		
		if(objects.size() == 1) {
			displayEdit(objects.get(0));
			return;
		}
		
		JCheckBox collidable = new JCheckBox("Collidable");
		collidable.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				panel.getCommandManager().execute(new MultiChangeCommand(objects, Change.Collidable, collidable.isSelected()));
			}
		});
		collidable.setSelected(objects.get(0).isCollidable());
		editPanel.add(collidable);
		
		boolean showColorChange = true;
		for(MapObject obj : objects) {
			if(obj instanceof RenderableMapObject) {
				if (((RenderableMapObject)obj).getColor() == null) {
					showColorChange = false;
					break;
				}
			}
		}
		
		if(showColorChange) {
			JButton color = new JButton("Color");
			color.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					Color newColor = JColorChooser.showDialog(contentPane, "Choose new color", ((RenderableMapObject) objects.get(0)).getColor());
					if(newColor != null) {
						color.setBackground(newColor);
						panel.getCommandManager().execute(new MultiChangeCommand(objects, Change.Color, newColor));
						repaint();
					}
				}
			});
			color.setBackground(((RenderableMapObject) objects.get(0)).getColor());
			editPanel.add(color);
		}
		
		revalidate();
		repaint();
	}
	
	public int getChosenTeam() {
		return this.chosenTeam;
	}
	
	public Color getChosenColor() {
		return this.chosenColor;
	}
	
	public int getChosenHealthRestore() {
		return this.restoreAmount;
	}
	
	public int getChosenCooldown() {
		return this.cooldown;
	}
	
	public int getChosen() {
		return this.chosen;
	}
	
	public int getAlign() {
		return this.align;
	}
	
	public Map getEditedMap() {
		return this.map;
	}
}
