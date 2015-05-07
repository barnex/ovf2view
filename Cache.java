import java.awt.image.*;
import java.util.concurrent.*;

// Cache provides transparently cached access to the image collection.
public final class Cache implements Runnable {

	static final int N_THREADS = 4;                                            // number of worker threads
	BufferedImage[] cache;
	ArrayBlockingQueue<Integer> requests = new ArrayBlockingQueue<Integer>(20);
	Canvas canvas;
	boolean started;

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
					BufferedImage img = IO.load(canvas.files[index]);
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
		return canvas.files.length;
	}

	synchronized BufferedImage get(int index) {
		assureStarted();
		if(index < 0 || index >= canvas.files.length) {
			Main.debug("index "+index+" out of bounds");
			return IO.brokenImage();
		}

		// TODO: proper init
		if(cache == null || cache.length != canvas.files.length) {
			cache = new BufferedImage[canvas.files.length];
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

	synchronized void assureStarted() {
		if (started) {
			return;
		}
		started = true;
		Main.debug("starting " + N_THREADS + " worker threads");
		for (int i=0; i<N_THREADS; i++) {
			Thread d = new Thread(this);
			d.setDaemon(true);
			d.start();
		}
	}

}
