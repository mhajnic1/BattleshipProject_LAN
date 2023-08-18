package brodici;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class Battleship extends JPanel {

	public Home home;
	public Board playerBoard;

	private JPanel gridPanel, playerPanel, myShipsPanel, invis;
	public JButton[][] gridButtonsPlayer;
	public UnclickableButton[][] gridButtonsMyShips;
	public UnclickableButton player1TurnButton, player2TurnButton;
	public boolean[][] buttonStates;
	public boolean playerTurn;
	public int turnCounter = 0, elapsedSeconds;

	public Timer gameTimer;
	public JLabel timerLabel;
	public long startTimeMillis;

	String opName;

	// test
	Color waveColor = new Color(39, 107, 172);
	ImageIcon seaTile = new ImageIcon(this.getClass().getResource("/ship/wavegif.gif"));
	ImageIcon fireTile = new ImageIcon(this.getClass().getResource("/ship/iks.gif"));
	ImageIcon rippleTile = new ImageIcon(this.getClass().getResource("/ship/ripple.gif"));

	ImageIcon headH = new ImageIcon(this.getClass().getResource("/ship/glavah.png"));
	ImageIcon bodyFH = new ImageIcon(this.getClass().getResource("/ship/tijelo1h.png"));
	ImageIcon bodySH = new ImageIcon(this.getClass().getResource("/ship/tijelo2h.png"));
	ImageIcon rearH = new ImageIcon(this.getClass().getResource("/ship/guzah.png"));
	ImageIcon headV = new ImageIcon(this.getClass().getResource("/ship/glava.png"));
	ImageIcon bodyFV = new ImageIcon(this.getClass().getResource("/ship/tijelo1.png"));
	ImageIcon bodySV = new ImageIcon(this.getClass().getResource("/ship/tijelo2.png"));
	ImageIcon rearV = new ImageIcon(this.getClass().getResource("/ship/guza.png"));

	public Battleship(Home home, Board playerBoard) {
		this.setBorder(new EmptyBorder(50, 0, 0, 0));
		this.home = home;
		this.playerBoard = playerBoard;

		gridPanel = new JPanel(new GridLayout(1, 3));

		playerPanel = new JPanel(new GridBagLayout());
		myShipsPanel = new JPanel(new GridBagLayout());
		invis = new JPanel(new GridBagLayout());

		gridButtonsPlayer = new JButton[10][10];
		gridButtonsMyShips = new UnclickableButton[10][10];

		buttonStates = new boolean[10][10];

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {

				gridButtonsPlayer[row][col] = new JButton();
				gridButtonsPlayer[row][col].setBorderPainted(false);
				gridButtonsPlayer[row][col].setPreferredSize(new Dimension(50, 50));
				gridButtonsPlayer[row][col].putClientProperty("row", row);
				gridButtonsPlayer[row][col].putClientProperty("col", col);
				gridButtonsPlayer[row][col].addActionListener(new ButtonClickListener());
				gridButtonsPlayer[row][col].setBackground(waveColor);
				gridButtonsPlayer[row][col].setIcon(seaTile);
				this.add(gridButtonsPlayer[row][col]);

				gridButtonsMyShips[row][col] = new UnclickableButton();
				gridButtonsMyShips[row][col].setBorderPainted(false);
				gridButtonsMyShips[row][col].setPreferredSize(new Dimension(50, 50));
				gridButtonsMyShips[row][col].putClientProperty("row", row);
				gridButtonsMyShips[row][col].putClientProperty("col", col);
				
				this.add(gridButtonsMyShips[row][col]);

			}
		}

		JLabel opponentLabel = new JLabel("Opponent's Ships");
		Font labelFont = new Font("Lucida Handwriting", Font.BOLD, 18);
		opponentLabel.setFont(labelFont);
		opponentLabel.setForeground(Color.RED);

		playerPanel.add(opponentLabel, createGBC(0, 0));
		playerPanel.add(createGridPanel(gridButtonsPlayer), createGBC(0, 1));

		Font font = new Font("Lucida Handwriting", Font.BOLD, 15);
		gameTimer = new Timer(1000, new TimerListener());
		timerLabel = new JLabel("Time: 00:00");
		timerLabel.setFont(font);
		timerLabel.setForeground(Color.RED);

		invis.setPreferredSize(new Dimension(160, 160));
		player1TurnButton = new UnclickableButton(
				System.getProperty("user.name").substring(0, System.getProperty("user.name").indexOf('.')));
		player2TurnButton = new UnclickableButton();

		Component filler = Box.createRigidArea(new Dimension(20, 20));
		Component filler2 = Box.createRigidArea(new Dimension(20, 20));

		player1TurnButton.setForeground(Color.RED);
		player1TurnButton.setFont(font);
		player2TurnButton.setForeground(Color.RED);
		player2TurnButton.setFont(font);

		Dimension buttonSize = new Dimension(150, 40);
		player1TurnButton.setPreferredSize(buttonSize);
		player2TurnButton.setPreferredSize(buttonSize);

		JLabel youLabel = new JLabel("My Ships");
		youLabel.setFont(labelFont);
		youLabel.setForeground(Color.RED);

		myShipsPanel.add(youLabel, createGBC(0, 0));
		myShipsPanel.add(createGridPanel(gridButtonsMyShips), createGBC(0, 1));

		gridPanel.setLayout(new GridBagLayout());

		gridPanel.add(playerPanel, createGBC(0, 0));
		gridPanel.add(invis, createGBC(1, 0));
		gridPanel.add(myShipsPanel, createGBC(2, 0));

		invis.add(player1TurnButton, createGBC(0, 0));
		invis.add(filler, createGBC(0, 1));
		invis.add(player2TurnButton, createGBC(0, 2));
		invis.add(filler2, createGBC(0, 3));
		invis.add(timerLabel, createGBC(0, 4));

		this.add(gridPanel);

	}

	public void refreshBoard() {
		
		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				if (playerBoard.grid[row][col] == 1) {
					for (Ship ship : playerBoard.getShips()) { // od
						for (int i = 0; i < ship.size; i++) {
							try {
								if (playerBoard.getShipAt(ship.row, ship.col).isHorizontal) {
									gridButtonsMyShips[ship.row][ship.col].setBackground(waveColor);
									gridButtonsMyShips[ship.row][ship.col + i].setBackground(waveColor);
									gridButtonsMyShips[ship.row][ship.col].setIcon(headH);
									gridButtonsMyShips[ship.row][ship.col + 1].setIcon(bodySH);
									gridButtonsMyShips[ship.row][ship.col + i].setIcon(bodyFH);
									gridButtonsMyShips[ship.row][ship.col
											+ playerBoard.getShipAt(ship.row, ship.col).size - 1].setIcon(rearH);
								} else {
									gridButtonsMyShips[ship.row][ship.col].setBackground(waveColor);
									gridButtonsMyShips[ship.row + i][ship.col].setBackground(waveColor);
									gridButtonsMyShips[ship.row][ship.col].setIcon(headV);
									gridButtonsMyShips[ship.row + 1][ship.col].setIcon(bodySV);
									gridButtonsMyShips[ship.row + i][ship.col].setIcon(bodyFV);
									gridButtonsMyShips[ship.row + playerBoard.getShipAt(ship.row, ship.col).size
											- 1][ship.col].setIcon(rearV);
								}
							} catch (ArrayIndexOutOfBoundsException e) {
								e.printStackTrace();
							}
						}
					}
					// gridButtonsMyShips[row][col].setBackground(Color.gray);
				} else {
					gridButtonsMyShips[row][col].setBackground(waveColor);
					gridButtonsMyShips[row][col].setIcon(seaTile);
				}
				
				if (playerBoard.grid[row][col] == 1) {
					gridButtonsMyShips[row][col].setEnabled(true);
				} else {
					gridButtonsMyShips[row][col].setEnabled(false);
				}
			}
		}
		player1TurnButton.setEnabled(playerTurn);
		player2TurnButton.setEnabled(!playerTurn);

		player2TurnButton.setText(opName.substring(0, opName.indexOf('.')));
	}

	private GridBagConstraints createGBC(int x, int y) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = x; // column
		gbc.gridy = y; // row
		return gbc;
	}

	private JPanel createGridPanel(JButton[][] gridButtons) {
		JPanel panel = new JPanel(new GridLayout(10, 10));
		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				panel.add(gridButtons[row][col]);
			}
		}
		return panel;
	}

	private class ButtonClickListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			JButton buttonClicked = (JButton) e.getSource();
			int row = (int) buttonClicked.getClientProperty("row");
			int col = (int) buttonClicked.getClientProperty("col");

			turnCounter++;
			buttonStates[row][col] = true;
			playerBoard.setPlayerButtonsEnabled(true, gridButtonsPlayer, buttonStates);
			home.network.send(new Message(row, col));

			playerTurn = !playerTurn;
			player1TurnButton.setEnabled(playerTurn);
			player2TurnButton.setEnabled(!playerTurn);
		}
	}

	private class TimerListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			long currentTimeMillis = System.currentTimeMillis();
			long elapsedMillis = currentTimeMillis - startTimeMillis;
			elapsedSeconds = (int) (elapsedMillis / 1000);
			timerLabel.setText("Time: " + formatTime(elapsedSeconds));
		}
	}

	private String formatTime(int seconds) {
		int minutes = seconds / 60;
		int remainingSeconds = seconds % 60;
		return String.format("%02d:%02d", minutes, remainingSeconds);
	}

	public void handleHit(boolean hit, int row, int col, boolean sink, Ship ship, boolean gameOver) {
		if (hit) {
			gridButtonsPlayer[row][col].setBackground(Color.orange); // Hit
			gridButtonsPlayer[row][col].setIcon(fireTile);

			try {
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/hit.wav")));
				clip.start();
			} catch (Exception e) {

			}

			if (sink) {
				handleSink(ship);
			}

		} else {
			// gridButtonsPlayer[row][col].setBackground(Color.blue); // Miss
			gridButtonsPlayer[row][col].setIcon(rippleTile);

			try {
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/unista.wav")));
				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(-10.0f);
				clip.start();
			} catch (Exception e) {

			}
		}

		if (gameOver) {
			gameTimer.stop();
			JOptionPane.showMessageDialog(getRootPane(), "You Won! Game Over.", "Game Over",
					JOptionPane.INFORMATION_MESSAGE);
			clearBoards();
			home.network.closeConnection();
			home.launchStartScreen();
		}
	}

	public void handleBeingHit(int row, int col) {
		if (playerBoard.isHit(row, col)) {
			playerBoard.grid[row][col] = 2; // Mark as hit
			gridButtonsMyShips[row][col].setBackground(Color.orange); // Hit
			// gridButtonsMyShips[row][col].setIcon(new
			// ImageIcon(this.getClass().getResource("/ship/hit.png")));

			try {
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/oof.wav")));
				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(-10.0f);
				clip.start();
			} catch (Exception e) {

			}

			Ship ship = playerBoard.getShipAt(row, col);

			if (playerBoard.isSunk(ship)) {
				handleBeingSunk(row, col, ship);
			} else {
				home.network.send(new Message(true, row, col, false, ship, false));
			}
		} else {
			// gridButtonsMyShips[row][col].setBackground(Color.blue); // Miss
			gridButtonsMyShips[row][col].setIcon(rippleTile);

			try {
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/miss.wav")));
				clip.start();
			} catch (Exception e) {

			}

			home.network.send(new Message(false, row, col, false, null, false));
		}

		playerTurn = !playerTurn;
		player1TurnButton.setEnabled(playerTurn);
		player2TurnButton.setEnabled(!playerTurn);
		playerBoard.setPlayerButtonsEnabled(false, gridButtonsPlayer, buttonStates);
	}

	public void handleSink(Ship ship) {
		if (ship.isHorizontal) {
			for (int i = ship.col; i < ship.col + ship.size; i++) {
				gridButtonsPlayer[ship.row][i].setBackground(Color.green); // Sunk
			}
		} else {
			for (int i = ship.row; i < ship.row + ship.size; i++) {
				gridButtonsPlayer[i][ship.col].setBackground(Color.green); // Sunk
			}
		}

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/explo.wav")));
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f);
			clip.start();
		} catch (Exception e) {

		}
	}

	public void handleBeingSunk(int row, int col, Ship ship) {
		if (ship.isHorizontal) {
			for (int i = ship.col; i < ship.col + ship.size; i++) {
				gridButtonsMyShips[ship.row][i].setBackground(Color.red); // Sunk
			}
		} else {
			for (int i = ship.row; i < ship.row + ship.size; i++) {
				gridButtonsMyShips[i][ship.col].setBackground(Color.red); // Sunk
			}
		}

		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/explo.wav")));
			FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
			gainControl.setValue(-10.0f);
			clip.start();
		} catch (Exception e) {

		}

		if (playerBoard.allSunk(playerBoard)) {

			gameTimer.stop();
			home.network.send(new Message(turnCounter, home.baza.getMyIp()));
			home.network.send(new Message(true, row, col, true, ship, true));

			try {
				Clip clip = AudioSystem.getClip();
				clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/sunk.wav")));
				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				gainControl.setValue(-10.0f);
				clip.start();
			} catch (Exception e) {

			}

			JOptionPane.showMessageDialog(getRootPane(), "You lost! Game Over.", "Game Over",
					JOptionPane.INFORMATION_MESSAGE);
			clearBoards();
			home.network.closeConnection();
			home.launchStartScreen();
		} else {
			home.network.send(new Message(true, row, col, true, ship, false));
		}
	}

	public void clearBoards() {
		turnCounter = 0;

		home.setup.resetBoard();

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				buttonStates[row][col] = false;
				gridButtonsPlayer[row][col].setBackground(waveColor);
                gridButtonsPlayer[row][col].setIcon(seaTile);
			}
		}
	}

	public class UnclickableButton extends JButton {
		public UnclickableButton(String text) {
			super(text);
		}

		public UnclickableButton() {
			super();
		}

		@Override
		protected void processMouseEvent(MouseEvent e) {
			if (isEnabled()) {
				return;
			}
			super.processMouseEvent(e);
		}
	}

}
