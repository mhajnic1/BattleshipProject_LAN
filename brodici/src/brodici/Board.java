package brodici;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.JButton;

public class Board {
	int[][] grid;

	public Board(int rows, int cols) {
		grid = new int[rows][cols];
	}

	public HashMap<Point, Ship> shipLocations = new HashMap<>();
	public List<Ship> placedShips = new ArrayList<>();

	public void placeShip(Ship ship) {
		if (ship.isHorizontal) {
			for (int i = ship.col; i < ship.col + ship.size; i++) {
				grid[ship.row][i] = 1; // 1 indicates a ship's part
				shipLocations.put(new Point(ship.row, i), ship);
			}
		} else {
			for (int i = ship.row; i < ship.row + ship.size; i++) {
				grid[i][ship.col] = 1; // 1 indicates a ship's part
				shipLocations.put(new Point(i, ship.col), ship);
			}
		}
		placedShips.add(ship);
	}

	public List<Ship> getShips() {
		return placedShips;
	}

	public Ship getShipAt(int row, int col) {
		return shipLocations.get(new Point(row, col));
	}

	public boolean isHit(int row, int col) {
		return grid[row][col] == 1;
	}

	public boolean isSunk(Ship ship) {
		if (ship.isHorizontal) {
			for (int i = ship.col; i < ship.col + ship.size; i++) {
				if (grid[ship.row][i] != 2) {
					return false; // If any part of the ship is not hit, return false
				}
			}
		} else {
			for (int i = ship.row; i < ship.row + ship.size; i++) {
				if (grid[i][ship.col] != 2) {
					return false; // If any part of the ship is not hit, return false
				}
			}
		}
		return true; // All parts of the ship have been hit
	}

	public boolean allSunk(Board board) {
		// Iterate through all ships and check if they are sunk
		for (Ship ship : board.getShips()) {
			if (!board.isSunk(ship)) {
				return false; // At least one ship is not sunk
			}
		}
		return true; // All ships are sunk
	}

	public void setPlayerButtonsEnabled(boolean playerTurn, JButton[][] gridButtonsPlayer, boolean[][] buttonStates) {
		for (int row = 0; row < 10; row++) {
			for (int col = 0; col < 10; col++) {
				if (playerTurn) {
					gridButtonsPlayer[row][col].setEnabled(false);
				} else {
					gridButtonsPlayer[row][col].setEnabled(true && !buttonStates[row][col]);
				}
			}
		}
	}
	
	public void clearShips() {
        for (int row = 0; row < 10; row++) {
            for (int col = 0; col < 10; col++) {
                grid[row][col] = 0;
            }
        }
        placedShips.clear();
        shipLocations.clear();
    }

}
