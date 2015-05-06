import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;

// Cache provides transparently cached access to the photo collection.
public final class Cache {

	static String[] extensions = new String[] {".jpeg", ".jpg"}; // extensions recognized by scan
	File[] photos = new File[0];                                 // scanned photos from dir
	BufferedImage[] cache;

	// total number of available photos
	int len() {
		return photos.length;
	}

	BufferedImage get(int index) {
		if(index < 0 || index >= photos.length) {
			Main.debug("index "+index+" out of bounds");
			return brokenImage();
		}

		if(cache[index] != null) {
			return cache[index];
		}

		try {
			Main.debug("load "+photos[index]);
			cache[index] = ImageIO.read(photos[index]);
			return cache[index];
		} catch(IOException e) {
			Main.debug(e.toString());
			return brokenImage();
		}
	}

	// Scans dir for photos
	void scan(File dir) {
		File[] files = dir.listFiles();
		if (files == null) {
			this.photos = new File[0];
		} else {
			Arrays.sort(files);
			ArrayList<File> photos = new ArrayList<File>();
			for (File f: files) {
				String name = f.getName().toLowerCase();
				for (String ext: extensions) {
					if (name.endsWith(ext)) {
						photos.add(f);
					}
				}
			}
			this.photos = photos.toArray(this.photos);
		}
		cache = new BufferedImage[photos.length];
		Main.debug("scan "+ dir+": " + this.photos.length+ " photos");
	}


	BufferedImage _brokenImage;
	static final int BROKEN_SIZE = 256;
	BufferedImage brokenImage() {
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
}
