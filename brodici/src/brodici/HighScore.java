package brodici;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class HighScore extends JPanel {

	public Home home;

	private ArrayList<String> players;
	DefaultTableModel tableModel;

	DefaultListModel<Korisnik> listModel = new DefaultListModel<>();

	private class BackgroundPanel extends JPanel {
		private Image backgroundImage;

		public BackgroundPanel(String imagePath) {
			backgroundImage = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
		}

		public void paintComponent(Graphics g) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
			// super.paintComponents(g);
		}
	}

	public HighScore(Home home) {

		this.home = home;
		players = home.baza.highScore();

		this.setLayout(null);

		BackgroundPanel backgroundPanel = new BackgroundPanel(home.config.path);
		backgroundPanel.setLayout(null);
		backgroundPanel.setBounds(0, 0, 1280, 720);

		String[] columnNames = { "Nick", "bodovi" };
		tableModel = new DefaultTableModel(columnNames, 0);
		JTable table = new JTable(tableModel) {
			public boolean isCellEditable(int row, int column) {
				return false; // da nije editable

			};
		};

		// Bodovi da su allignani u desno
		DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
		table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);

		table.getTableHeader().setReorderingAllowed(false); // Disable column reordering
		table.getTableHeader().setResizingAllowed(false); // Disable column resizing

		table.getTableHeader().setBounds(420, 70, 350, 30);
		table.setBounds(420, 200, 350, 200);
		JScrollPane scp = new JScrollPane(table);
		scp.setBounds(420, 200, 350, 200);

		this.add(scp);
		// this.add(table);
		this.add(table.getTableHeader());
		this.add(backgroundPanel);

		(new Thread() {
			public void run() {
				while (true) {
					players = home.baza.highScore();

					SwingUtilities.invokeLater(() -> {
						tableModel.setRowCount(0); // Clear previous data

						for (String player : players) {
							String[] playerInfo = player.split(" ");
							playerInfo[0] = playerInfo[0].substring(0, playerInfo[0].indexOf('.'));
							tableModel.addRow(playerInfo);

						}
					});

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();

		JButton highScore = new JButton("Main screen");
		// tableModel.isCellEditable(ERROR, ABORT);

		backgroundPanel.add(highScore);
		highScore.setBounds(500, 110, 200, 40);
		highScore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				home.launchStartScreen();
			}
		});

	}

}
