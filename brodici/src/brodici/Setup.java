package brodici;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class Setup extends JPanel {
	private JButton[][] gridButtons;

	public Home home;
	public Board playerBoard;
	public boolean player1ready = false, player2ready = false;

	private int[][] board = new int[10][10];
	private int[] shipSizes = { 6, 4, 4, 3, 3, 3, 2, 2, 2, 2 }; // Sizes of the ships

	private boolean placingShips = true;
	private int currentShipIndex = 0;
	private int currentShipSize = shipSizes[currentShipIndex];
	private boolean isHorizontal = true;
	private Color hoverColor = null;

	// NOVO
	JButton startButton = new JButton("Start");
	boolean freshGame = true;
	int shipCounter = shipSizes.length;
	JLabel shipsRemainingLabel = new JLabel();

	// sea color, image
	Color waveColor = new Color(39, 107, 172);
	ImageIcon seaTile = new ImageIcon(this.getClass().getResource("/ship/wavegif.gif"));
	ImageIcon splash = new ImageIcon(this.getClass().getResource("/ship/wavegifsplash.gif"));
	// ship images
	ImageIcon headH = new ImageIcon(this.getClass().getResource("/ship/glavah.png"));
	ImageIcon bodyFH = new ImageIcon(this.getClass().getResource("/ship/tijelo1h.png"));
	ImageIcon bodySH = new ImageIcon(this.getClass().getResource("/ship/tijelo2h.png"));
	ImageIcon rearH = new ImageIcon(this.getClass().getResource("/ship/guzah.png"));
	ImageIcon headV = new ImageIcon(this.getClass().getResource("/ship/glava.png"));
	ImageIcon bodyFV = new ImageIcon(this.getClass().getResource("/ship/tijelo1.png"));
	ImageIcon bodySV = new ImageIcon(this.getClass().getResource("/ship/tijelo2.png"));
	ImageIcon rearV = new ImageIcon(this.getClass().getResource("/ship/guza.png"));

	public Setup(Home home, Board playerBoard) {
		this.playerBoard = playerBoard;
		this.home = home;

		gridButtons = new JButton[10][10];

		initializeBoard();
		createGUI();

		playerBoard = new Board(10, 10);
	}

	private void initializeBoard() {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = 0;
			}
		}
	}

	private void createGUI() {
		// this.setLayout(new BorderLayout());
		JPanel holder = new JPanel(new BorderLayout());
		JPanel boardPanel = new JPanel(new GridLayout(10, 10));
		gridButtons = new JButton[10][10];

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				gridButtons[row][col] = new JButton();
				gridButtons[row][col].setPreferredSize(new Dimension(50, 50));
				gridButtons[row][col].setBackground(waveColor);
				gridButtons[row][col].setIcon(seaTile);
				gridButtons[row][col].setBorder(BorderFactory.createLineBorder(waveColor, 1));
				gridButtons[row][col].putClientProperty("row", row);
				gridButtons[row][col].putClientProperty("col", col);
				gridButtons[row][col].addMouseListener(new EnterMouseListener());
				boardPanel.add(gridButtons[row][col]);
			}
		}

		// this.add(boardPanel, BorderLayout.CENTER);

		this.setBorder(new EmptyBorder(110, 0, 0, 0));

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setPreferredSize(new Dimension(200, 200));
		rightPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

		// rotate button
		JButton rotateButton = new JButton("Rotate (Horizontal)");
		rotateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				toggleOrientation();
				rotateButton.setText(isHorizontal ? "Rotate" : "Rotate (Vertical)");
			}
		});

		// NOVO
		shipsRemainingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		shipsRemainingLabel.setText("Remaining ships: " + shipCounter);
		rightPanel.add(shipsRemainingLabel);
		rightPanel.add(Box.createVerticalGlue());
		// ---

		rotateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		rotateButton.setMaximumSize(new Dimension(150, rotateButton.getPreferredSize().height));
		rotateButton.setBorder(new EmptyBorder(20, 20, 20, 20));
		rightPanel.add(rotateButton);

		rightPanel.add(Box.createRigidArea(new Dimension(0, 10))); // razamak između reset i rotate

		// reset button
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				resetBoard();
				startButton.setEnabled(false);// N O V O
				resetRemainingShips();
			}
		});

		resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		resetButton.setMaximumSize(new Dimension(150, resetButton.getPreferredSize().height));
		resetButton.setBorder(new EmptyBorder(20, 20, 20, 20));
		rightPanel.add(resetButton);

		rightPanel.add(Box.createVerticalGlue()); // dugi box između reset i start

		// Start button

		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				home.network.send(new Message("ready"));
				home.battleship.refreshBoard();
				home.network.waitForOponent();
			}
		});

		startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		startButton.setMaximumSize(new Dimension(150, startButton.getPreferredSize().height));
		startButton.setBorder(new EmptyBorder(20, 20, 20, 20));

		rightPanel.add(startButton);
		startButton.setEnabled(false);

		// holder.setBackground(Color.red);
		Border bevelBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
		holder.setBorder(bevelBorder);
		holder.setBounds(500, 110, 200, 40);
		holder.setAlignmentX(Component.CENTER_ALIGNMENT);
		holder.add(rightPanel, BorderLayout.EAST);
		holder.add(boardPanel, BorderLayout.CENTER);
		// holder.setBorder(new EmptyBorder(0, 150, 0, 0));
		this.add(holder);

		// this.add(rightPanel, BorderLayout.EAST);
	}

	private void resetRemainingShips() {
		shipCounter = 10;
		shipsRemainingLabel.setText("Remaining ships: " + shipCounter);
	}

	public void resetBoard() {

		playerBoard.clearShips();
		resetRemainingShips();

		// reset velicinu brodova
		placingShips = true;
		currentShipIndex = 0;
		currentShipSize = shipSizes[currentShipIndex];

		startButton.setEnabled(false);

		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				board[i][j] = 0;
			}
		}

		clearBoardHighlight(gridButtons);

		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				gridButtons[row][col].setBackground(waveColor);
				gridButtons[row][col].setIcon(seaTile);
			}
		}
	}

	private void clearBoardHighlight(JButton[][] grid) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				grid[i][j].setBorder(BorderFactory.createLineBorder(waveColor, 1));
			}
		}
	}

	private void finishBoard(JButton[][] grid) {
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (board[i][j] != 1) {
					grid[i][j].setBackground(waveColor);
					grid[i][j].setIcon(seaTile);
				}
			}
		}
	}

	public class EnterMouseListener extends MouseAdapter { // highlight miša on enter
		@Override
		public void mouseEntered(MouseEvent e) {
			JButton btn = (JButton) e.getSource();
			int row = (int) btn.getClientProperty("row");
			int col = (int) btn.getClientProperty("col");
			// paintHoverOutline(row, col);
			if (placingShips) {
				paintAreaCheck(row, col);
				paintHoverOutline(row, col);
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JButton btn = (JButton) e.getSource();

			int row = (int) btn.getClientProperty("row");
			int col = (int) btn.getClientProperty("col");

			if (currentShipSize > 0) {
				if (!isHorizontal) {
					for (int i = 0; i < currentShipSize; i++) {
						if (row + i < 10) {
							gridButtons[row + i][col].setBorder(BorderFactory.createLineBorder(waveColor, 1));
						}
					}
				} else {
					for (int i = 0; i < currentShipSize; i++) {
						if (col + i < 10) {
							gridButtons[row][col + i].setBorder(BorderFactory.createLineBorder(waveColor, 1));
						}
					}
				}
			}
			if (placingShips) {
				clearBoardHighlight(gridButtons);
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			JButton button = (JButton) e.getSource();
			int row = (int) button.getClientProperty("row");
			int col = (int) button.getClientProperty("col");
			if (SwingUtilities.isRightMouseButton(e)) {
				toggleOrientation();
				clearBoardHighlight(gridButtons);
				paintAreaCheck(row, col);
				paintHoverOutline(row, col);
			}
			if (SwingUtilities.isLeftMouseButton(e)) {

				if (placingShips) {
					clearBoardHighlight(gridButtons);
					placeBlockField(row, col);
					placeShip(row, col);
					paintHoverOutline(row, col);
					shipsRemainingLabel.setText("Remaining ships: " + shipCounter);
				}

				if (currentShipIndex == 10) {
					finishBoard(gridButtons);
					startButton.setEnabled(true);
				}
			}
		}

	}

	private void paintAreaCheck(int row, int col) {
		if (placingShips && currentShipSize > 0) {
			for (int i = 0; i < currentShipSize; i++) {
				if (board[row][col] != 1 && canPlaceBlockField(row, col)) { // ZELENO AKO SE MOŽE STAVITI, CRVENO AKO NE
					hoverColor = Color.yellow;
				} else {
					hoverColor = Color.red;
				}
				if (isHorizontal) {
					if (col != 0) {
						gridButtons[row][col - 1].setBorder(BorderFactory.createLineBorder(hoverColor, 4)); // left
						if (!canPlaceShip(row, col) && canPlaceBlockField(row, col)) {
							paintAreaCheck(row, col - 1);
						}
					}
					if (col + currentShipSize < 10) {
						gridButtons[row][col + currentShipSize]
								.setBorder(BorderFactory.createLineBorder(hoverColor, 4));
						// if (!canPlaceShip(row, col) && canPlaceBlockField(row, col))
						// paintAreaCheck(row, col+1); // right
					}
					if (col + i < 10 /* && (row+1) < 10 && (row-1) >= 0 */) { // izbjegava exception catch
																				// ArrayIndexOutOfBoundsException
						if ((row + 1) < 10) {
							gridButtons[row + 1][col + i].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
						}
						if ((row - 1) >= 0) {
							gridButtons[row - 1][col + i].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
						}
					}
				} else {
					if (row != 0) {
						gridButtons[row - 1][col].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
						if (!canPlaceShip(row, col) && canPlaceBlockField(row, col)) {
							paintAreaCheck(row - 1, col);
						}
					}
					if (row + currentShipSize < 10) {
						gridButtons[row + currentShipSize][col]
								.setBorder(BorderFactory.createLineBorder(hoverColor, 4));
					} // test
					if (row + i < 10) { // sprječava izlazak index-a van granice
						if ((col + 1) < 10) {
							gridButtons[row + i][col + 1].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
						}
						if ((col - 1) >= 0) {
							gridButtons[row + i][col - 1].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
						}
					}
				}
			}
		}
	}

	private void paintHoverOutline(int row, int col) {

		if (placingShips && currentShipSize > 0) {
			if (!isHorizontal) {
				for (int i = 0; i < currentShipSize; i++) {

					if (board[row][col] != 1 && canPlaceBlockField(row, col)) { // ZELENO AKO SE MOŽE STAVITI, CRVENO
																				// AKO NE - VERTIKALNO
						hoverColor = Color.green;
					} else {
						hoverColor = Color.red;
					}

					if (row + i < 10) {
						gridButtons[row + i][col].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
					}
					if (!canPlaceShip(row, col) && canPlaceBlockField(row, col) && board[row][col] != 1) {
						paintHoverOutline(row - 1, col);
					}
				}
			} else {
				for (int i = 0; i < currentShipSize; i++) {

					if (board[row][col] != 1 && canPlaceBlockField(row, col)) { // ZELENO AKO SE MOŽE STAVITI, CRVENO
																				// AKO NE - VERTIKALNO
						hoverColor = Color.green;
					} else {
						hoverColor = Color.red;
					}

					if (col + i < 10) {
						gridButtons[row][col + i].setBorder(BorderFactory.createLineBorder(hoverColor, 4));
					}

					// ne crta se ako se može staviti brod, crta ako se može blockfield, ne crta ako
					// je brod postavljen na plocu
					if (!canPlaceShip(row, col) && canPlaceBlockField(row, col) && board[row][col] != 1) {
						paintHoverOutline(row, col - 1);
					}
				}
			}
		}
	}

	private void toggleOrientation() { // mijenja orijentaciju broda
		isHorizontal = !isHorizontal;
	}

	private boolean canPlaceBlockField(int row, int col) {
		if (isHorizontal) {

			for (int i = 0; i < currentShipSize; i++) {
				if (col != 0) { // lijevi check
					if (board[row][col - 1] == 1) {
						return false;
					}
				}
				if (col + currentShipSize < 10) { // desni check
					if (board[row][col + currentShipSize] == 1) {
						return false;
					}
				}
				if (col + i < 10 /* && (row+1) < 10 && (row-1) >= 0 */) {
					if ((row + 1) < 10) { // bottom check
						if (board[row + 1][col + i] == 1) {
							return false;
						}
					}
					if ((row - 1) >= 0) { // top check
						if (board[row - 1][col + i] == 1) {
							return false;
						}
					}
				}
			}
		} else {

			for (int i = 0; i < currentShipSize; i++) {
				if (row != 0) {
					if (board[row - 1][col] == 1) {
						return false;
					}
				}
				if (row + currentShipSize < 10) {
					if (board[row + currentShipSize][col] == 1) {
						return false;
					}
				} // test
				if (row + i < 10) { // sprječava izlazak index-a van granice
					if ((col + 1) < 10) {
						if (board[row + i][col + 1] == 1) {
							return false;
						}

					}
					if ((col - 1) >= 0) {
						if (board[row + i][col - 1] == 1) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

	private void placeBlockField(int row, int col) {

		if (canPlaceBlockField(row, col)) {
			if (isHorizontal) {
				for (int i = 0; i < currentShipSize; i++) {
					if (col != 0) {
						gridButtons[row][col - 1].setBackground(waveColor); // left
						gridButtons[row][col - 1].setIcon(seaTile);
						if (!canPlaceShip(row, col)) {
							placeBlockField(row, col - 1);
						}
					}
					if (col + currentShipSize < 10) {
						gridButtons[row][col + currentShipSize].setBackground(waveColor); // right
						gridButtons[row][col + currentShipSize].setIcon(seaTile);
					}
					if (col + i < 10) { // izbjegava exception catch ArrayIndexOutOfBoundsException
						if ((row + 1) < 10) {
							gridButtons[row + 1][col + i].setBackground(waveColor); // bottom
							gridButtons[row + 1][col + i].setIcon(seaTile);
						}
						if ((row - 1) >= 0) {
							gridButtons[row - 1][col + i].setBackground(waveColor); // top
							gridButtons[row - 1][col + i].setIcon(seaTile);
						}
					}
				}
			} else {
				for (int i = 0; i < currentShipSize; i++) {
					if (row != 0) {

						gridButtons[row - 1][col].setBackground(waveColor); // test
						gridButtons[row -1 ][col].setIcon(seaTile);
						if (!canPlaceShip(row, col)) {
							placeBlockField(row - 1, col);
						}
					}
					if (row + currentShipSize < 10) {

						gridButtons[row + currentShipSize][col].setBackground(waveColor);
						gridButtons[row +currentShipSize][col].setIcon(seaTile);
					} // test
					if (row + i < 10) { // sprječava izlazak index-a van granice
						if ((col + 1) < 10) {

							gridButtons[row + i][col + 1].setBackground(waveColor);
							gridButtons[row + i][col + 1].setIcon(seaTile);
						}
						if ((col - 1) >= 0) {

							gridButtons[row + i][col - 1].setBackground(waveColor);
							gridButtons[row + i][col - 1].setIcon(seaTile);
						}
					}
				}
			}
		}
	}

	private void placeShip(int row, int col) {

		if (canPlaceShip(row, col) && canPlaceBlockField(row, col)) {
			for (int i = 0; i < currentShipSize; i++) {
				if (isHorizontal) {
					board[row][col + i] = 1;
					
					gridButtons[row][col + i].setBackground(waveColor);
					gridButtons[row][col].setIcon(headH);
					gridButtons[row][col+1].setIcon(bodySH);
					gridButtons[row][col+i].setIcon(bodyFH);
					gridButtons[row][col+currentShipSize-1].setIcon(rearH);
					
					//gridButtons[row][col + i].setBackground(Color.lightGray);
				} else {
					board[row + i][col] = 1;
							
					gridButtons[row + i][col].setBackground(waveColor);
					gridButtons[row][col].setIcon(headV);
					gridButtons[row+1][col].setIcon(bodySV);
					gridButtons[row+i][col].setIcon(bodyFV);
					gridButtons[row+currentShipSize-1][col].setIcon(rearV);
				}
				
				try {
				    Clip clip = AudioSystem.getClip();
				    clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/splash.wav")));
				    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(-15.0f);
				    clip.start();
				   
				} catch (Exception e) {
				}
			}

			playerBoard.placeShip(new Ship(currentShipSize, isHorizontal, row, col));
			shipCounter--;
			currentShipIndex++;

			if (currentShipIndex < shipSizes.length) {
				currentShipSize = shipSizes[currentShipIndex];
			} else {
				placingShips = false;
			}
		} else if (canPlaceBlockField(row, col)) {
			// JOptionPane.showMessageDialog(frame, "Ovdje se ne moze staviti brod.");

			if (isHorizontal) {
				placeShip(row, col - 1);
			} else { ///// AKO JE BROD NA RUBU POMIČE BROD DA STANE NA BOARD
				placeShip(row - 1, col);
			}
		}
	}

	private boolean canPlaceShip(int row, int col) {
		int maxRow = isHorizontal ? row : row + currentShipSize - 1;
		int maxCol = isHorizontal ? col + currentShipSize - 1 : col;

		// Check if the calculated maximum row or column indices are within valid bounds
		if (maxRow >= 10 || maxCol >= 10) {
			// messageChange(rotateButton, "Prelazi polje! Odaberi ponovno!");
			return false;
		}

		// PROVJERA ZA OVERLAPPING
		for (int i = 0; i < currentShipSize; i++) {

			int newRow = row;
			int newCol = col;

			if (!isHorizontal) {
				newRow = row + i; // Calculate new row for vertical placement
			} else {
				newCol = col + i; // Calculate new column for horizontal placement
			}

			if (board[newRow][newCol] == 1) {
				return false;
			}
		}
		return true;
	}
}
