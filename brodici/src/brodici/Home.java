package brodici;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.swing.*;

public class Home {

	public JFrame frame;

	Baza baza;
	InitBaze initBaze;
	Network network;
	Config config;
	Board board;
	StartScreen startScreen;
	Setup setup;
	Battleship battleship;
	HighScore highScore;

	public void init() {
		initBaze = new InitBaze("oracle.jdbc.driver.OracleDriver",
				"/*your database*/", "/*your username for the database*/", "/*your password for the database*/");
		baza = new Baza(initBaze);
		network = new Network(this);
		config = new Config();
		board = new Board(10, 10);
		startScreen = new StartScreen(this);
		setup = new Setup(this, board);
		battleship = new Battleship(this, board);
		highScore = new HighScore(this);

		startScreen.setBounds(0, 0, config.width, config.height);
		setup.setBounds(0, 0, config.width, config.height);
		battleship.setBounds(0, 0, config.width, config.height);

		frame.getContentPane().add(startScreen);
		frame.getContentPane().add(setup);
		frame.getContentPane().add(battleship);
		frame.getContentPane().add(highScore);

		(new Thread() {
			public void run() {
				try {

					Clip clip = AudioSystem.getClip();
					clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/shanty2.wav")));
					FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
					gainControl.setValue(-25.0f);

					clip.start();
					clip.loop(Clip.LOOP_CONTINUOUSLY);

				} catch (Exception e) {

				}
			}
		}).start();

	}

	public Home() {
		frame = new JFrame();
		init();
		frame.setTitle(
				"Pucanje u brodiće - epska bitka potapanja brodića koja potjece iz vremena antike i nikoga ne ostavlja ravnodusnim, unistava ali i sklapa nova prijateljstva medu ljudima svih dobnih i rasnih skupina pa cak i Sike you thought");
		// frame.setTitle("Battleship");
		frame.setSize(config.width, config.height);
		frame.setResizable(false);
		frame.setVisible(true);

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				try {
					network.send(new Message("connection lost"));
					network.closeConnection();
				} catch (Exception exception) {
					exception.printStackTrace();
					frame.dispose();
				} finally {
					System.exit(0);
				}
			}
		});
	}

	public void launchStartScreen() {
		baza.inGame("0");
		startScreen.setVisible(true);
		setup.setVisible(false);
		battleship.setVisible(false);
		highScore.setVisible(false);
		this.frame.repaint();
	}

	public void launchSetup() {
		baza.inGame("1");
		startScreen.setVisible(false);
		setup.setVisible(true);
		frame.setSize(config.width, config.height);
		this.frame.repaint();
	}

	public void launchBattleship() {
		setup.setVisible(false);
		battleship.setVisible(true);
		frame.setSize(config.width, config.height);
		this.frame.repaint();
	}

	public void launchHighScore() {
		startScreen.setVisible(false);
		highScore.setVisible(true);
		this.frame.repaint();
	}

	public void launchDenied() {
		JOptionPane.showMessageDialog(null, "Your request is denied!", "Popup Message",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public void launchDisconnected() {
		JOptionPane.showMessageDialog(null, "Your opponnent has disconnected!", "Popup Message",
				JOptionPane.INFORMATION_MESSAGE);
	}

	public static void main(String[] args) {
		Home m = new Home();
		m.launchStartScreen();
	}
}
