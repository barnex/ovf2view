import java.io.*;
import javax.swing.*;

public final class Main {

	static final int FRAME_W = 800, FRAME_H = 600; // initial window size

	public static void main(String[] args) throws IOException {

		final JFrame f = new JFrame();
		f.setFocusable(true);
		f.setSize(FRAME_W, FRAME_H);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});


		String dir = ".";
		if (args.length > 0) {
			dir = args[0];
		}
		Canvas p = new Canvas();
		p.scan(new File(dir));
		//p.dispNext(0);

		f.add(p);

		f.setVisible(true);
	}

	static void debug(String msg) {
		System.out.println(msg);
	}
}
