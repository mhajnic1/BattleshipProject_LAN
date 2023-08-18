package brodici;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;

@SuppressWarnings("serial")
public class Popup extends JFrame {
	Home home;

	public Popup(Home home, String name) {
		this.home = home;
		setTitle("Popup Frame");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		try {
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/sounds/violina.wav")));
			clip.start();
		} catch (Exception e) {

		}

		int choice = JOptionPane.showOptionDialog(home.frame, name.substring(0, name.indexOf('.')) + " challanged you to a game!", "Invitation",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] { "Accept", "Decline" },
				"Accept");

		if (choice == JOptionPane.YES_OPTION) {
			home.network.send(new Message("accepted"));
			home.launchSetup();

		} else if (choice == JOptionPane.NO_OPTION || choice == JOptionPane.CLOSED_OPTION) {
			home.network.send(new Message("request denied"));
			home.network.closeConnection();
		}

	}
}
