package FishingGame;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Fish extends JPanel{
	
 	private Image fish;
 	int x = -90;
 	int y;
 	Random random;
 	boolean catched;
 	
 	public Fish() throws IOException {
 		fish = ImageIO.read(getClass().getResource("/Image/fish.png"));
 		random = new Random();
 		this.y = random.nextInt(190) + 263;
 		
 	}
 	
 	public void paint(Graphics g) {
 		g.drawImage(fish, this.x, this.y, null);
 	}
 	
 	public void setImg(String name) throws IOException {
 		fish = ImageIO.read(getClass().getResource(name));
 	}
 	
 	public void step() throws IOException {
 		if(!catched) {
 			this.x = this.x + 1;
 		}
 		if(x > 725) {
 			this.x = -50;
 			this.y = random.nextInt(250) + 263;
 		}
 	}
 	
 	public boolean catchFish(FishingLine line) {
 		if((this.x >= 260 && this.x <= 310) && (line.y >= this.y - 22 && line.y <= this.y + 31)) {
 			catched = true;
 			return true;
 		}
 		return false;
 	}
 	
 	public static void main(String[] arg) throws IOException {
 		JFrame frame = new JFrame();
 		Fish fish = new Fish();
 		//new Thread(fish).start();
 		frame.setSize(725, 580);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(3);
		frame.setLocationRelativeTo((Component)null);
		frame.add(fish);
		frame.setVisible(true);
 		
 	}

	
}
