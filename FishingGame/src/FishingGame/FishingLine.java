package FishingGame;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.Line;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FishingLine extends JPanel{

	Image fishingLine2;
	int y;
	
	public FishingLine() throws IOException{
		fishingLine2 = ImageIO.read(getClass().getResource("/Image/fishingLine2.png"));
		y = 360;
	}
	
	public void paint(Graphics g) {
		super.paintComponent(g); // call the superclass's paint Method
		g.setColor(Color.WHITE);
		g.drawLine(319,  160,  319, y);
		g.drawImage(fishingLine2, 310, y, null);
	}
	
	public void paintCut(Graphics g) {
		super.paintComponent(g); // call the superclass's paint Method
		g.setColor(Color.WHITE);
		g.drawLine(319,  160,  319, y);
	}
	
	public static void main(String[] arg) throws IOException, InterruptedException {
 		JFrame frame = new JFrame();
 		FishingLine line = new FishingLine();
 		frame.setSize(725, 580);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		frame.setLocationRelativeTo((Component)null);
		frame.add(line);
		frame.setVisible(true);
 		
 	}
}
