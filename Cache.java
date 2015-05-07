import java.awt.image.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

// Cache provides transparently cached access to the image collection.
public final class Cache implements Runnable {

	static String[] extensions = new String[] {".jpeg", ".jpg"}; // extensions recognized by scan
	File[] images = new File[0];                                 // scanned images from dir
	BufferedImage[] cache;
	ArrayBlockingQueue<Integer> requests = new ArrayBlockingQueue<Integer>(20);
	Canvas canvas;

	Cache(Canvas c) {
		canvas = c;
	}

	public void run() {
		for(;;) {
			try {
				int index = requests.take().intValue();
				boolean need;
				synchronized(this) {
					need = cache[index] == null;
				}
				if (need) {
					BufferedImage img = IO.load(images[index]);
					synchronized(this) {
						cache[index] = img;
					}
					canvas.repaint(); // TODO: repaint only if available image visible
				}
			} catch(InterruptedException e) {
				Main.debug(e.toString());
			}
		}
	}

	// total number of available images
	int len() {
		return images.length;
	}

	synchronized BufferedImage get(int index) {
		if(index < 0 || index >= images.length) {
			Main.debug("index "+index+" out of bounds");
			return IO.brokenImage();
		}

		if(cache[index] != null) {
			return cache[index];
		} else {
			//try {
			Main.debug("requesting image #"+index);
			boolean ok = requests.offer(new Integer(index));
			Main.debug("requesting image #"+index+":OK="+ok);
			//} catch(InterruptedException e) {
			//	Main.debug(e.toString());
			//}
			return IO.loadingImage();
		}
	}



	// Scans dir for images
	void scan(File dir) {
		File[] files = dir.listFiles();
		if (files == null) {
			this.images = new File[0];
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
			this.images = images.toArray(this.images);
		}
		cache = new BufferedImage[images.length];
		Main.debug("scan "+ dir+": " + this.images.length+ " images");
	}


}
