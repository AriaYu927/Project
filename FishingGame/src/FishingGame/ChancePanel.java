package FishingGame;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;


public class ChancePanel extends JPanel{
	private Image d;
	
	public ChancePanel(int num) throws IOException {
		if(num >=0)
			this.d = ImageIO.read(getClass().getResource("/Image/chance"+num+".png"));
		else
			this.d = ImageIO.read(getClass().getResource("/Image/chance3.png"));
	}
	
	 public void paint(Graphics g) {
		    g.drawImage(this.d,65, 148, null);
	 }
}
