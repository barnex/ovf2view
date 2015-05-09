import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.*;

public final class Img {

	final File file;  // source file
	boolean loading;          // cache already started loading this image?
	int w, h;                 // (full) image size, if known
	BufferedImage full;       // full resolution, if available
	BufferedImage thumb;      // thumbnail resolution, if available

	Img(File f) {
		file = f;
	}

	void setImage(BufferedImage i) {
		BufferedImage th = Render.resize(i, Main.MAX_THUMB_SIZE, Main.MAX_THUMB_SIZE);
		Main.debug(file + " thumb size: "+ th.getWidth()+ " x "+th.getHeight());
		synchronized(this) {
			full = i;
			thumb = th;
		}
	}

	// draw thumbnail
	void drawThumb(Graphics2D g, int thumbsize) {
		drawFileName(g, thumbsize);

		BufferedImage th;
		synchronized(this) {
			th = thumb;
		}
		if(th==null) {
			drawMissing(g, thumbsize);
			Main.worker.request(this);
		} else {
			drawImg(g, th, thumbsize);
		}
	}

	// draw image, scale to thumbsize
	void drawImg(Graphics2D g, BufferedImage image, int thumbsize) {
		double w = (double)(thumbsize);          // canvas size
		double h = (double)(thumbsize);
		double imw = (double)(image.getWidth());  // image size
		double imh = (double)(image.getHeight());

		double zx = w / imw;                      // zoom to fit
		double zy = h / imh;
		double zoom = (zx < zy? zx: zy);

		Main.debug("zoom="+zoom);

		double tx = (w - imw*zoom)/2;             // translate to center
		double ty = (h - imh*zoom)/2;
		AffineTransform transf = g.getTransform();
		transf.translate(tx, ty);
		transf.scale(zoom, zoom);
		g.setTransform(transf);
		g.drawImage(image, 0, 0,   null);
	}


	// draw the file name
	void drawFileName(Graphics2D g, int thumbsize) {
		String name = file.getName();
		FontMetrics font = g.getFontMetrics();
		int a = font.getAscent();
		int w = font.stringWidth(name);
		g.setColor(Main.foreground);
		int offx = (thumbsize-w)/2;
		if (offx < 0) {
			offx = 0;
		}
		g.drawString(name, offx, thumbsize+a+1);
	}

	// draw placeholder for thumbnail with missing image (e.g.: still loading).
	void drawMissing(Graphics2D g, int thumbsize) {
		g.setColor(Main.foreground);
		g.drawRect(0, 0, thumbsize, thumbsize);
	}

	public String toString() {
		return file.toString();
	}

}
