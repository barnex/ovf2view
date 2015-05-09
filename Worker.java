import java.awt.image.*;
import java.util.concurrent.*;

// Worker asynchronously loads images in the background
public final class Worker implements Runnable {

	static final int N_THREADS =  Runtime.getRuntime().availableProcessors();
	ArrayBlockingQueue<Img> requests = new ArrayBlockingQueue<Img>(N_THREADS);
	Canvas canvas;
	boolean started;

	Worker(Canvas c) {
		canvas = c;
	}

	synchronized void request(Img i) {
		Main.debug("request " + i.toString());
		assureStarted();
		requests.offer(i);
	}

	// run a worker thead take requested Img from requests,
	// and load its images
	public void run() {
		for(;;) {
			try {
				Img img = requests.take();
				synchronized(img) {
					if(img.loading) {
						continue;
					}
					img.loading = true;
				}
				BufferedImage i = IO.load(img.file);
				img.setImage(i);
				canvas.repaint(); // TODO: repaint only if available image visible

			} catch(InterruptedException e) {
				Main.debug(e.toString());
			}
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
