package FishingGame;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Crab extends JPanel{

	private Image crab;
 	int x = -500;
 	int y;
 	Random random;
 	boolean cut;
 	
 	public Crab() throws IOException {
 		crab = ImageIO.read(getClass().getResource("/Image/crab.png"));
 		random = new Random();
 		this.y = random.nextInt(160) + 263;
 		
 	}
 	
 	public void paint(Graphics g) {
 		g.drawImage(crab, this.x, this.y, null);
 	}
 	
 	public void step() throws IOException, InterruptedException {
 		this.x++;
 		if(x > 725) {
 			this.x = -500;
 			this.y = random.nextInt(250) + 263;
 		}
 	}
 	
 	public boolean cutLine(FishingLine line) {
 		if((this.x >= 250 && this.x < 320) && line.y > this.y) {
 			cut = true;
 			return true;
 		}
 		return false;
 	}
 	
 	public static void main(String[] arg) throws IOException {
 		JFrame frame = new JFrame();
 		Crab crab = new Crab();
 		frame.setSize(725, 580);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		frame.setLocationRelativeTo((Component)null);
		frame.add(crab);
		frame.setVisible(true);
 		
 	}
}
