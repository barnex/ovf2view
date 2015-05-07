import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

public final class IO {

	static String[] extensions = new String[] {".jpeg", ".jpg"}; // extensions recognized by scan

	// Scans dir for images
	static File[] scan(File dir) {
		File[] files = dir.listFiles();
		if (files == null) {
			return new File[0];
		} else {
			Arrays.sort(files);
			ArrayList<File> images = new ArrayList<File>();
			for (File f: files) {
				String name = f.getName().toLowerCase();
				for (String ext: extensions) {
					if (name.endsWith(ext)) {
						images.add(f);
					}
				}
			}
			return images.toArray(new File[0]);
		}
	}


	static BufferedImage load(File file) {
		Main.debug("thread " + Thread.currentThread().getId()+" loading "+file);
		try {
			return ImageIO.read(file);
		} catch(IOException e) {
			Main.debug(e.toString());
			return brokenImage();
		}
	}

	static BufferedImage _brokenImage;
	static final int BROKEN_SIZE = 256;
	static synchronized BufferedImage brokenImage() {
		if (_brokenImage != null) {
			return _brokenImage;
		}
		_brokenImage = new BufferedImage(BROKEN_SIZE, BROKEN_SIZE, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = (Graphics2D)(_brokenImage.getGraphics());
		g.setColor(Color.RED);
		g.drawLine(0, 0, BROKEN_SIZE, BROKEN_SIZE);
		g.drawLine(0, BROKEN_SIZE, BROKEN_SIZE, 0);
		return _brokenImage;
	}

	static synchronized BufferedImage loadingImage() {
		return brokenImage();//TODO
	}
}
