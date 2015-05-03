import java.awt.*;
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
	BufferedImage image;                                         // current photo
	RenderingHints hints;                                        // quality settings

	// New Canvas wit directory to scan
	Canvas(String dir) {
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, 	RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_DITHERING, 	    RenderingHints.VALUE_DITHER_DISABLE);
		hints.put(RenderingHints.KEY_INTERPOLATION,    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_RENDERING, 	    RenderingHints.VALUE_RENDER_QUALITY);

		this.dir = new File(dir);
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

	void loadImg(int i) throws IOException {
		if(photos.length == 0) {
			this.image = null;
			return;
		}
		this.image = ImageIO.read(photos[i]);
	}

	public void paintComponent(Graphics g_) {

		super.paintComponent(g_);
		Graphics2D g = (Graphics2D)(g_);
		g.setRenderingHints(hints);


		if (this.image != null) {
			int dx = (getWidth() - image.getWidth())/2;
			int dy = (getHeight() - image.getHeight())/2;
			g.drawImage(this.image, null, dx, dy);
		}

	}

	private static final long serialVersionUID = 1;
}

