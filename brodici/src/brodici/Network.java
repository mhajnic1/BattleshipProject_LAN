package brodici;

import com.google.gson.Gson;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.SwingUtilities;

public class Network {
	Home home;

	Socket socket;
	ServerSocket serverSocket;
	DataInputStream din;
	DataOutputStream dout;

	boolean ready = false;

	Network(Home home) {
		this.home = home;

		(new Thread() {
			public void run() {
				Server();
			}
		}).start();
	}

	public void Server() {

		do {
			try {
				serverSocket = new ServerSocket(9990);
				Socket sockett= serverSocket.accept();
				if (socket == null) {
					socket = sockett;
					// openPopup();
					(new Thread() {
						public void run() {
							try {
								din = new DataInputStream(socket.getInputStream());
								dout = new DataOutputStream(socket.getOutputStream());
								Chat(true);
							} catch (IOException e) {
							}
						}
					}).start();
				} else {
					Socket b;
					b = serverSocket.accept();
					DataOutputStream doutt = new DataOutputStream(b.getOutputStream());
					doutt.writeUTF("request denied");
					doutt.flush();
					b.close();
				}
			} catch (Exception e) {
			}
		} while (true);
	}

	void openPopup(String name) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Popup(home, name); // Create and show the popup frame
			}
		});
	}

	public void Client1(String ip) {
		try {
			socket = new Socket(ip, 9990);
			(new Thread() {
				public void run() {
					try {
						din = new DataInputStream(socket.getInputStream());
						dout = new DataOutputStream(socket.getOutputStream());
						Chat(false);
					} catch (IOException e) {
					}
				}

			}).start();
		} catch (Exception e) {
		}
	}

	public void send(Message m) {
		try {
			Gson gson = new Gson();
			dout.writeUTF(gson.toJson(m));
			dout.flush();
		} catch (Exception e) {
		}
	}

	public void closeConnection() {
		try {
			dout.close();
		} catch (Exception e) {
		}

		try {
			din.close();
		} catch (Exception e) {
		}

		try {
			serverSocket.close();
		} catch (Exception e) {
		}

		try {
			socket.close();
			socket = null;
		} catch (Exception e) {
		}

	}

	public void waitForOponent() {
		try {
			waitForReady();
			home.launchBattleship();
			home.battleship.startTimeMillis = System.currentTimeMillis();
			home.battleship.gameTimer.start();
		} catch (InterruptedException e) {
		}
		ready = false;
	}

	public synchronized void waitForReady() throws InterruptedException {
		while (!ready) {
			wait();
		}
	}

	public synchronized void setReady() {
		ready = true;
		notifyAll(); // Notify waiting threads
	}

	public void Chat(boolean playerTurn) throws IOException {
		home.board.setPlayerButtonsEnabled(playerTurn, home.battleship.gridButtonsPlayer, home.battleship.buttonStates);
		home.battleship.playerTurn = !playerTurn;

		Gson gson = new Gson();
		Message m;
		
		send(new Message(System.getProperty("user.name"), 0));
		
		do {
			String receivedMsg = din.readUTF();
			m = gson.fromJson(receivedMsg, Message.class);

			if (m.gameOver == null) {
				m.gameOver = false;
			}

			switch (m.event) {
			case ("shoot"):
				home.battleship.handleBeingHit(m.row, m.col);
				break;
			case ("odgovor"):
				home.battleship.handleHit(m.hit, m.row, m.col, m.sink, m.ship, m.gameOver);
				break;
			case ("accepted"):
				home.launchSetup();
				break;
			case ("request denied"):
				home.launchDenied();
				closeConnection();
				break;
			case ("game over"):
				int opTurns = m.turns;
				String myid = home.baza.getMyIp();
				String opid = m.ip;
				home.baza.insertIgra(new Igra(home.baza.dohvatiKorisnika(opid), home.baza.dohvatiKorisnika(myid),
						opTurns, home.battleship.turnCounter, home.battleship.elapsedSeconds));
				break;
			case ("connection lost"):
				closeConnection();
				home.launchDisconnected();
				home.launchStartScreen();
				home.battleship.clearBoards();
				break;
			case ("ready"):
				setReady();
				break;
			case("name"):
				home.battleship.opName = m.name;
				if (playerTurn) openPopup(m.name);
				break;
			default:
				break;
			}
		}while(!m.gameOver);

	this.closeConnection();
}}