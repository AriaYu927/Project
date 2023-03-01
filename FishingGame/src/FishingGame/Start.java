package FishingGame;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.*;

public class Start extends JPanel{
	boolean start = false;
	boolean gameover = false;
	boolean catched = false;
	boolean clickBasket = false;
	boolean cut = false;
	boolean clickChance = false;
	
	private Image background;
	private Image background2;
	private Image startImg;
	private Image person;
	
	private int Id;
	private static int nextId = 0;
	
	int score = 0;
	int chance = 3;
	
	private ChancePanel chancePanel;
	private ScorePanel scorePanel;
	private FishingLine line;
	private Fish leftFish;
	private Crab crab;
	private Shark shark;

	private Random random;
	
	
	public Start() throws IOException {
		
		this.Id = this.nextId;
		this.nextId++;
		background2 = ImageIO.read(getClass().getResource("/Image/background2.png"));
		background = ImageIO.read(getClass().getResource("/Image/background.jpg"));
		
		startImg = ImageIO.read(getClass().getResource("/Image/play.png"));
		person = ImageIO.read(getClass().getResource("/Image/person.png"));
		leftFish = new Fish();
		crab = new Crab();
		shark = new Shark();
	    line = new FishingLine();
	    chancePanel = new ChancePanel(chance);
	    scorePanel = new ScorePanel(0,0);
	    
	    
		random = new Random();
		
		
	}
	
	public void paint(Graphics g) {
		if(!gameover) {
			g.drawImage(this.background2, 0, 2, null);
			g.drawImage(this.background, 0, 130, null);
			
			try {
				scorePanel = new ScorePanel(score / 10,  score % 10);
				scorePanel.paint(g);
				chancePanel = new ChancePanel(chance);
				chancePanel.paint(g);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(cut)
				line.paintCut(g);
			else if(!cut)
				line.paint(g);
			g.drawImage(this.person, 200, 60, null);
		}
		if(!this.start && !gameover) {
	    	g.drawImage(this.startImg, 235, 290, null);
	    }
	    
	    
	    if(gameover) {
			try {
				Image end = ImageIO.read(getClass().getResource("/Image/gameover.png"));
				g.drawImage(end, 0, 130, null);
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	    if(this.start && clickBasket == false) {
	    	leftFish.paint(g);
	    	crab.paint(g);
	    	shark.paint(g);
	    	if(catched) {
	    		try {
					leftFish.setImg("/Image/getFish.png");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    	}
	    }
	    else if(clickBasket == true) {
	    	
	    	try {
				leftFish = new Fish();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	clickBasket = false;
	    	catched = false;
	    	
	    }
	}
	
	
	public void action() throws InterruptedException, IOException {
		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!start && !gameover) {
					Start.this.start = true;
					Start.this.repaint();
					System.out.println("StartImg: start = "+start + ", gameover = " + gameover);
				}
				else if(start && !gameover) {
					
					Point p = e.getPoint();
					if(cut && p.x >= 0  &&p.x <= 60 && p.y <= 170 && p.y >= 130) {
						cut = false;
						chance--;
						System.out.println("chance = " + chance + " " + clickChance);
					}
					Start.this.repaint();
				}
				else if(gameover) {
					Start.this.leftFish.x = -90;
					Start.this.crab.x = -500;
					Start.this.shark.x = -4000;
					
					Start.this.score = 0;
					Start.this.gameover = false;
					Start.this.start = false;
					
					Start.this.repaint();
					System.out.println("endImg: start = "+start + ", gameover = " + gameover);
					
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
	    	
	    });
		
		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point p = e.getPoint();
				if(start && p.y > 156) {
					line.y = p.y - 22;
					if(!cut) {
						if(Start.this.leftFish.catchFish(line)) {
							catched = true;					
						}
						if(catched) {
							leftFish.x = 290;
							leftFish.y = line.y;
						}	
					}
					
					Start.this.repaint();
				}				
			}
		});
		
		while(true) {
			if(this.start) {
				this.leftFish.step();
				this.crab.step();
				this.shark.step();
				if(crab.cutLine(line) || shark.cutLine(line))
					this.cut = true;
				if(chance <= 0 && cut) {
					cut = false;
					gameover = true;
					start = false;
					chance = 3;
				}
			}
			repaint();
			Thread.sleep(8);
		}
	}
}
