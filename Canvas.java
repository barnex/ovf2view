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

	static String[] extensions = new String[] {".jpeg", ".jpg"}; // extensions recognized by scan
	File[] photos = new File[0];                                 // scanned photos from dir

	//int currentimg;                                              // currently displayed photo
	//BufferedImage image;                                         // current photo
	//AffineTransform transf = new AffineTransform();              // transform on current image

	int W, H;                                                    // canvas size
	int nx, ny;
	Color background = Color.DARK_GRAY;                           // background color
	RenderingHints hints;                                        // quality settings

	// New Canvas with directory to scan
	Canvas() {
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

		initEvents();
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
		Main.debug("scan "+ dir+": " + this.photos.length+ " photos");
	}

	//void zoomFit() {
	//	if (image == null) {
	//		return;
	//	}

	//	double w = (double)(getWidth());          // canvas size
	//	double h = (double)(getHeight());
	//	double imw = (double)(image.getWidth());  // image size
	//	double imh = (double)(image.getHeight());

	//	double zx = w / imw;                      // zoom to fit
	//	double zy =  h /imh;
	//	double zoom = (zx < zy? zx: zy);
	//	transf.setToScale(zoom, zoom);

	//	double tx = (w/zoom - imw)/2;             // translate to center
	//	double ty = (h/zoom - imh)/2;
	//	transf.translate(tx, ty);
	//}


	int thumbsize = 256;
	int border = 1;
	static final int MIN_THUMB_SIZE = 32;

	void zoom(int delta) {
		if(delta > 0) {
			thumbsize /= 2;
		}
		if(delta < 0) {
			thumbsize *= 2;
		}
		if(thumbsize < MIN_THUMB_SIZE) {
			thumbsize = MIN_THUMB_SIZE;
		}
		thumbsize = min(thumbsize, W, H);
		sizesChanged();
		repaint();
	}

	int scrollPos;

	void scroll(int delta) {
		if(photos.length == 0 || nx == 0) {
			return;
		}

		// scroll one row
		scrollPos += delta * nx;
		// don't scroll out of bounds
		if (scrollPos < 0) {
			scrollPos = 0;
		}
		if(scrollPos >= photos.length) {
			scrollPos = photos.length - 1;
		}
		Main.debug("scrollpos:"+ scrollPos);
		repaint();
	}

	int getScrollLine() {
		if (nx == 0) {
			return 0;
		}
		return scrollPos / nx;
	}

	void sizesChanged() {
		W = getWidth();
		H = getHeight();

		// divide in grid nx * ny
		nx = W / thumbsize;
		ny = H / thumbsize;
		if (nx == 0) {
			nx = 1;
		}
		if (ny == 0) {
			ny = 1;
		}
	}


	int repaintCount;
	public void paintComponent(Graphics g_) {
		repaintCount++;
		sizesChanged();  // repaint may be called before resize event...

		Main.debug("repaint #" + repaintCount);
		// reset graphics
		Graphics2D g = (Graphics2D)(g_);
		g.setClip(0, 0, W, H);

		// clear background
		g.setColor(background);
		g.fillRect(0, 0, W, H);
		g.setRenderingHints(hints);


		// center grid in frame
		int stridex = W / nx;
		int stridey = H / ny;
		int offx = (stridex - thumbsize) / 2;
		int offy = (stridey - thumbsize) / 2;

		for (int i=0; i<nx; i++) {
			for(int j=0; j<ny; j++) {
				int photo = (j+getScrollLine())*nx+i;
				if(photo >= photos.length) {
					continue;
				}
				int x = i*W/nx+offx;
				int y = j*H/ny+offy;
				g.setTransform(AffineTransform.getTranslateInstance(x, y));
				g.setClip(0, 0, thumbsize, thumbsize);
				paintThumb(g, photo);
			}
		}

		//zoomFit();
		//if (this.image != null) {
		//	g.drawImage(this.image, transf, null);
		//}

	}

	void paintThumb(Graphics2D g, int photo) {

		//BufferedImage image = loadImg(photo);
		BufferedImage image = loadImg(photo);
		double w = (double)(thumbsize);          // canvas size
		double h = (double)(thumbsize);
		double imw = (double)(image.getWidth());  // image size
		double imh = (double)(image.getHeight());

		double zx = w / imw;                      // zoom to fit
		double zy = h / imh;
		double zoom = (zx < zy? zx: zy);

		double tx = (w - imw*zoom)/2;             // translate to center
		double ty = (h - imh*zoom)/2;
		AffineTransform transf = g.getTransform();
		transf.translate(tx, ty);
		transf.scale(zoom, zoom);
		g.setTransform(transf);
		g.drawImage(image, 0, 0,   null);
	}

	BufferedImage loadImg(int index) {
		if(index < 0 || index >= photos.length) {
			Main.debug("index "+index+" out of bounds");
			return brokenImage();
		}
		try {
			Main.debug("load "+photos[index]);
			return ImageIO.read(photos[index]);
		} catch(IOException e) {
			Main.debug(e.toString());
			return brokenImage();
		}
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

//void dispNext(int delta) {
//	if(photos.length == 0) {
//		image = null;
//		return;
//	}
//	currentimg+=delta;
//	while(currentimg < 0) {
//		currentimg += photos.length;
//	}
//	while(currentimg >= photos.length) {
//		currentimg -= photos.length;
//	}

//	disp(photos[currentimg]);
//}

//void disp(File photo) {
//	Main.debug("loading: " + photo);
//	try {
//		this.image = ImageIO.read(photo);
//	} catch(IOException e) {
//		throw new IllegalArgumentException(e);//TODO
//	}
//	repaint();
//}

	void initEvents() {
		//getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		//getActionMap().put("left", new AbstractAction() {
		//	public void actionPerformed(ActionEvent e) {
		//		dispNext(-1);
		//	}
		//	private static final long serialVersionUID = 1; // sigh...
		//});

		//getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		//getActionMap().put("right", new AbstractAction() {
		//	public void actionPerformed(ActionEvent e) {
		//		dispNext(1);
		//	}
		//	private static final long serialVersionUID = 1;
		//});

		//getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "right");

		addMouseListener(new MouseAdapter() {

		});
		addMouseMotionListener(new MouseMotionAdapter() {

		});
		addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				if((e.getModifiers() == InputEvent.CTRL_MASK)) {
					zoom(e.getWheelRotation());
				}
				if((e.getModifiers() == 0)) {
					scroll(e.getWheelRotation());
				}
			}
		});
		addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				sizesChanged();
			}
		});
	}

	int min(int a, int b, int c) {
		return min(a, min(b, c));
	}
	int min(int a, int b) {
		if (a<b) {
			return a;
		} else {
			return b;
		}
	}

	private static final long serialVersionUID = 1;
}

