package brodici;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class StartScreen extends JPanel {

	public Home home;

	private ArrayList<Korisnik> nikoviLikovi;

	DefaultListModel<Korisnik> listModel = new DefaultListModel<>();

	private class BackgroundPanel extends JPanel {
		private Image backgroundImage;

		public BackgroundPanel(String imagePath) {
			backgroundImage = new ImageIcon(this.getClass().getResource(imagePath)).getImage();
		}

		public void paintComponent(Graphics g) {
			g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
		}
	}

	public StartScreen(Home home) {
		this.home = home;

		this.setLayout(null);

		BackgroundPanel backgroundPanel = new BackgroundPanel(home.config.path);
		backgroundPanel.setLayout(null);
		backgroundPanel.setBounds(0, 0, home.config.width, home.config.height);

		JPanel mainPanel = new JPanel(new BorderLayout());
		mainPanel.setBounds(500, 200, 200, 200);

		JList<Korisnik> displayList = new JList<>(listModel);
		displayList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);

		displayList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try {
					if (e.getClickCount() == 2) { // Detect double click
						String index = displayList.getSelectedValue().getIp();
						home.network.Client1(index);
					}
				} catch (Exception ex) {

				}
			}
		});

		this.add(backgroundPanel);
		backgroundPanel.add(mainPanel);
		mainPanel.add(displayList, BorderLayout.NORTH);

		(new Thread() {
			public void run() {
				while (true) {

					home.baza.setActive();
					nikoviLikovi = home.baza.onlineKorisnici();
					listModel.clear();

					/*
					 * for (Korisnik item : nikoviLikovi) { listModelBuffer.addElement(item); }
					 */

					listModel.addAll(nikoviLikovi);

					try {
						Thread.sleep(2000);
					} catch (Exception e) {

					}
				}
			}
		}).start();

		JButton highScore = new JButton("High score");

		backgroundPanel.add(highScore);
		highScore.setBounds(500, 110, 200, 40);
		highScore.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				home.launchHighScore();
			}
		});
	}

}
