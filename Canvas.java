import java.awt.*;
import javax.swing.*;
import java.awt.image.*;

final class Canvas extends JComponent{

	BufferedImage image;
	RenderingHints hints;

	Canvas(){
 		hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_COLOR_RENDERING, 	RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_DITHERING, 	    RenderingHints.VALUE_DITHER_DISABLE);
		hints.put(RenderingHints.KEY_INTERPOLATION,    RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_RENDERING, 	    RenderingHints.VALUE_RENDER_QUALITY);
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

