import java.awt.event.*;
import java.awt.*;
import java.io.*;
import javax.swing.*;

public final class Main {

	static final int FRAME_W = 800, FRAME_H = 600; // initial window size
	static Color background = Color.DARK_GRAY;
	static Color foreground = Color.WHITE;
	static JLabel statusLabel = new JLabel();
	static final int MAX_THUMB_SIZE = 480;
	static final int MIN_THUMB_SIZE = 48;
	static Worker worker;

	public static void main(String[] args) throws IOException {

		final JFrame f = new JFrame();
		f.setFocusable(true);
		f.setSize(FRAME_W, FRAME_H);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});
		f.setBackground(background);
		f.setForeground(foreground);


		String dir = ".";
		if (args.length > 0) {
			dir = args[0];
		}

		Canvas c = new Canvas();
		c.setBackground(background);
		c.scan(new File(dir));

		worker = new Worker(c);

		statusLabel.setOpaque(true);
		statusLabel.setBackground(background);
		statusLabel.setForeground(foreground);

		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(c, BorderLayout.CENTER);
		f.getContentPane().add(statusLabel, BorderLayout.SOUTH);

		f.setVisible(true);
	}


	static void debug(String msg) {
		statusLabel.setText(msg);
		System.out.println(msg);
	}
}
