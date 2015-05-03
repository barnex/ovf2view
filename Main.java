import javax.swing.*;
import java.awt.event.*;

public final class Main {

	static final int FRAME_W = 800, FRAME_H = 600; // initial window size
	static String dir = "/home/arne/picsZZ";         // directory to look for files

	public static void main(String[] args) {

		final JFrame f = new JFrame();
		f.setSize(FRAME_W, FRAME_H);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});

		Canvas p = new Canvas(dir);
		f.add(p);

		f.setVisible(true);
	}

	static void debug(String msg) {
		System.out.println(msg);
	}
}
