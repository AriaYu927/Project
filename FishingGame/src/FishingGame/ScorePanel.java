package FishingGame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ScorePanel extends JPanel{
	Image basket;
	Image d1;
	Image d2;
	
	public ScorePanel(int num1, int num2) throws IOException {
		basket = ImageIO.read(getClass().getResource("/Image/basket.png"));
		d1 = ImageIO.read(getClass().getResource("/Image/score" + num1 + ".png"));
		d2 = ImageIO.read(getClass().getResource("/Image/score" + num2 + ".png"));
	}
	public void paint(Graphics g) {
		g.drawImage(this.basket, 355, 40+130, null);
		g.drawImage(this.d1, 387, 67+130, null);
		g.drawImage(this.d2, 401, 67+130, null);
	}
	
	public static void main(String[] arg) throws IOException {
		JFrame frame = new JFrame();
		ScorePanel scorep = new ScorePanel(9,5);
		frame.setSize(725, 580);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		frame.setLocationRelativeTo((Component)null);
		frame.add(scorep);
		frame.setVisible(true);
	}
}
