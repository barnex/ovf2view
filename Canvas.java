import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.*;
import javax.swing.*;

// Canvas displays the image list.
final class Canvas extends JComponent {

	int thumbsize = 256;
	int border = 10;
	static final int MIN_THUMB_SIZE = 32;
	int W, H;                             // canvas size
	Img[] files = new Img[0];
	Cache cache;

	// New Canvas with directory to scan
	Canvas() {
		cache = new Cache(this);
		initEvents();
	}

	// scan dir for displayable files,
	// store in files.
	void scan(File dir) {
		File[] ls = IO.scan(dir);
		files = new Img[ls.length];
		for(int i=0; i<files.length; i++) {
			files[i] = new Img(ls[i]);
		}
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
		int nx = W / (thumbsize+2*border);
		if (nx == 0) {
			nx = 1;
		}
		return nx;
	}


	// grid size x
	int ny() {
		int ny = H / (thumbsize+2*border);
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

		Graphics2D g = (Graphics2D)(g_);

		FontMetrics fm = g.getFontMetrics();
		if(border <= fm.getAscent() + fm.getDescent()) {
			border = fm.getAscent() + fm.getDescent() + 1;
		}

		sizesChanged();  // repaint may be called before resize event...

		// clear background
		g.setColor(Main.background);
		g.fillRect(0, 0, W, H);
		g.setRenderingHints(Render.hints);

		g.setColor(Main.foreground);

		// center grid in frame
		int stridex = W / nx();
		int stridey = H / ny();
		int offx = (stridex - (thumbsize+border)) / 2;
		int offy = (stridey - (thumbsize+border)) / 2;

		for (int i=0; i<nx(); i++) {
			for(int j=0; j<ny(); j++) {
				int index = (j+getScrollLine())*nx()+i;
				if(index >= nImg()) {
					continue;
				}
				int x = i*W/nx()+offx;
				int y = j*H/ny()+offy;
				g.setTransform(AffineTransform.getTranslateInstance(x, y));
				g.setColor(Main.foreground);
				g.setClip(0, 0, thumbsize+border, thumbsize+border);
				files[index].drawThumb(g, thumbsize);
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

