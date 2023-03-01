package FishingGame;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Shark extends JPanel{

	private Image shark;
 	int x = -4000;
 	int y;
 	Random random;
 	boolean cut;
 	
 	public Shark() throws IOException {
 		shark = ImageIO.read(getClass().getResource("/Image/shark.png"));
 		random = new Random();
 		this.y = random.nextInt(140) + 263;
 		
 	}
 	
 	public void paint(Graphics g) {
 		g.drawImage(shark, this.x, this.y, null);
 	}
 	
 	public void step() throws IOException, InterruptedException {
 		this.x++;
 		if(x > 725) {
 			this.x = -5000;
 			this.y = random.nextInt(250) + 263;
 		}
 	}
 	
 	public boolean cutLine(FishingLine line) {
 		if((this.x >= 60 && this.x < 110) && line.y > this.y) {
 			cut = true;
 			return true;
 		}
 		return false;
 	}
 	
 	public static void main(String[] arg) throws IOException {
 		JFrame frame = new JFrame();
 		Shark shark = new Shark();
 		frame.setSize(725, 580);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		frame.setLocationRelativeTo((Component)null);
		frame.add(shark);
		frame.setVisible(true);
 		
 	}
}
