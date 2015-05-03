import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public final class Main {

	static final int FRAME_W = 800, FRAME_H = 600; // initial window size
	static String dir = "/home/arne/pics";         // directory to look for files

	public static void main(String[] args) throws IOException {

		final JFrame f = new JFrame();
		f.setSize(FRAME_W, FRAME_H);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});

		Canvas p = new Canvas(dir);
		p.scan();
		p.loadImg(0);

		f.add(p);

		f.setVisible(true);
	}

	static void debug(String msg) {
		System.out.println(msg);
	}
}
