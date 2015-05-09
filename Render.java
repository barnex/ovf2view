import java.awt.image.*;
import java.awt.*;
import java.awt.geom.*;

public final class Render {

	static RenderingHints hints;                   // quality settings
	static {
		init();
	}


	static BufferedImage scale(BufferedImage src, int w, int h) {
		double zx = (double)(w) / (double)(src.getWidth());
		double zy = (double)(h) / (double)(src.getHeight());
		double zoom = (zx < zy? zx: zy);

		AffineTransform transf = AffineTransform.getScaleInstance(zoom, zoom);

		int newW = (int)((double)(src.getWidth() * zoom));
		int newH = (int)((double)(src.getHeight() * zoom));
		BufferedImage dst = new BufferedImage(newW, newH, BufferedImage.TYPE_3BYTE_BGR);

		Image smooth = src.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);

		Graphics2D g = (Graphics2D)(dst.getGraphics());
		g.setRenderingHints(hints);
		//g.setTransform(transf);
		g.drawImage(smooth, 0, 0, null);
		return dst;
	}

	static void init() {
		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
	}

}
