import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

// Canvas displays the photo list.
final class Canvas extends JComponent {

	File dir = new File("");                                     // directory to scan
	static String[] extensions = new String[] {".jpeg", ".jpg"}; // extensions recognized by scan
	File[] photos = new File[0];                                 // scanned photos from dir
	int currentimg;                                              // currently displayed photo
	BufferedImage image;                                         // current photo
	Color background = Color.BLACK;                              // background color
	AffineTransform transf = new AffineTransform();              // transform on current image
	RenderingHints hints;                                        // quality settings

	// New Canvas wit directory to scan
	Canvas(String dir) {
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, 	RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_DITHERING, 	    RenderingHints.VALUE_DITHER_DISABLE);
		hints.put(RenderingHints.KEY_INTERPOLATION,    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_RENDERING, 	    RenderingHints.VALUE_RENDER_QUALITY);

		this.dir = new File(dir);

		initEvents();
	}

	// Scan the current directory for photos
	void scan() {
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
		Main.debug("scan "+ dir+": " + this.photos.length+ " photos");
	}

	void zoomFit() {
		if (image == null) {
			return;
		}

		double w = (double)(getWidth());          // canvas size
		double h = (double)(getHeight());
		double imw = (double)(image.getWidth());  // image size
		double imh = (double)(image.getHeight());

		double zx = w / imw;                      // zoom to fit
		double zy =  h /imh;
		double zoom = (zx < zy? zx: zy);
		transf.setToScale(zoom, zoom);

		double tx = (w/zoom - imw)/2;             // translate to center
		double ty = (h/zoom - imh)/2;
		transf.translate(tx, ty);
	}

	public void paintComponent(Graphics g_) {

		//super.paintComponent(g_);
		Graphics2D g = (Graphics2D)(g_);
		g.setColor(background);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setRenderingHints(hints);

		zoomFit();

		if (this.image != null) {
			g.drawImage(this.image, transf, null);
		}

	}

	void dispNext(int delta) {
		if(photos.length == 0) {
			image = null;
			return;
		}
		currentimg+=delta;
		currentimg %= photos.length;
		try {
			this.image = ImageIO.read(photos[currentimg]);
		} catch(IOException e) {
			todo
		}
	}

	void initEvents() {
		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		getActionMap().put("left", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispNext(-1);
			}
			private static final long serialVersionUID = 1; // sigh...
		});

		getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		getActionMap().put("right", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				dispNext(1);
			}
			private static final long serialVersionUID = 1;
		});

		addMouseListener(new MouseAdapter() {

		});
		addMouseMotionListener(new MouseMotionAdapter() {

		});
	}


	private static final long serialVersionUID = 1;
}

