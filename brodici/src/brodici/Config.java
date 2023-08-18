package brodici;

import java.io.IOException;
import java.io.FileReader;

import org.ini4j.*;

public class Config {
	public Ini aRead;

	public int width;
	public int height;
	public String path;

	public Config() {
		try {
			aRead = new Ini();
			aRead.load(new FileReader("res/settings.ini"));

			width = Integer.parseInt(aRead.get("FrameSize", "width"));
			height = Integer.parseInt(aRead.get("FrameSize", "height"));
			path = aRead.get("WallpaperPath", "path");

		} catch (IOException e) {
			System.err.println(e.getMessage());
		}

	}

}
