import javax.swing.*;
import java.awt.event.*;

public final class Main{

	static final int FRAME_W = 800, FRAME_H = 600; // default window size
	
	public static void main(String[] args){

		final JFrame f = new JFrame();
		f.setSize(FRAME_W, FRAME_H);
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				f.dispose();
			}
		});

		Canvas p = new Canvas();
		f.add(p);

		f.setVisible(true);
	}
}
