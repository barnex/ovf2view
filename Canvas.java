import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

// Canvas displays the image list.
final class Canvas extends JComponent {

	int thumbsize = 256;
	int border = 1;
	static final int MIN_THUMB_SIZE = 32;
	int W, H;                             // canvas size

	Color background = Color.DARK_GRAY;   // background color

	File[] files = new File[0];
	Cache cache;

	// New Canvas with directory to scan
	Canvas() {
		cache = new Cache(this);
		initEvents();
	}

	void scan(File dir) {
		files = IO.scan(dir);
	}

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
		if(nImg() == 0 || ny() == 0) {
			return;
		}

		// scroll one row
		scrollPos += delta * nx();
		// don't scroll out of bounds
		if (scrollPos < 0) {
			scrollPos = 0;
		}
		if(scrollPos >= nImg()) {
			scrollPos = nImg() - 1;
		}
		repaint();
	}

	int getScrollLine() {
		if (nx() == 0) {
			return 0;
		}
		return scrollPos / nx();
	}

	void sizesChanged() {
		W = getWidth();
		H = getHeight();

	}

	// grid size x
	int nx() {
		int nx = W / thumbsize;
		if (nx == 0) {
			nx = 1;
		}
		return nx;
	}


	// grid size x
	int ny() {
		int ny = H / thumbsize;
		if (ny == 0) {
			ny = 1;
		}
		return ny;
	}

	// total number of images in library
	int nImg() {
		return files.length;
	}


	int repaintCount;
	//Timer paintTimer = new Timer();

	public void paintComponent(Graphics g_) {
		repaintCount++;
		long start = now();
		sizesChanged();  // repaint may be called before resize event...

		// reset graphics
		Graphics2D g = (Graphics2D)(g_);
		g.setClip(0, 0, W, H);

		// clear background
		g.setColor(background);
		g.fillRect(0, 0, W, H);
		g.setRenderingHints(Render.hints);


		// center grid in frame
		int stridex = W / nx();
		int stridey = H / ny();
		int offx = (stridex - thumbsize) / 2;
		int offy = (stridey - thumbsize) / 2;

		for (int i=0; i<nx(); i++) {
			for(int j=0; j<ny(); j++) {
				int image = (j+getScrollLine())*nx()+i;
				if(image >= nImg()) {
					continue;
				}
				int x = i*W/nx()+offx;
				int y = j*H/ny()+offy;
				g.setTransform(AffineTransform.getTranslateInstance(x, y));
				g.setClip(0, 0, thumbsize, thumbsize);
				paintThumb(g, image);
			}
		}

		Main.debug("repaint #" + repaintCount + ": " + since(start));

		//zoomFit();
		//if (this.image != null) {
		//	g.drawImage(this.image, transf, null);
		//}

	}

	long now() {
		return System.currentTimeMillis();
	}

	String since(long start) {
		return (now() - start) + "ms";
	}

	void paintThumb(Graphics2D g, int index) {
		BufferedImage image = cache.get(index);
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

