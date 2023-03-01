package FishingGame;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Player extends JFrame{
	Start start;
	// Datagram socket
	DatagramSocket socket;
	// The byte array for sending and receiving datagram packets
	byte[] buf = new byte[256];
	// Server InetAddress
	InetAddress address;
	// The packet sent to the server
	DatagramPacket sendPacket;
	// The packet received from the server
	DatagramPacket receivePacket;
	
	ByteArrayOutputStream baos;
    ObjectOutputStream oos;
	
	private JLabel label = new JLabel("ThisPlayer: 0");
	private JLabel label1 = new JLabel("No other players");
	
	public Player() throws IOException, InterruptedException {
		label.addMouseListener(new ClickListener());
		label.addMouseMotionListener(new MoveListener());
		label1.addMouseListener(new ClickListener());
		label1.addMouseMotionListener(new MoveListener());
		
		
		JPanel topPanel = new JPanel();
		label1.setPreferredSize(new Dimension(580,15));
		topPanel.add(label1);
		topPanel.add(label);
		topPanel.setSize(725, 15);
		this.add(topPanel,BorderLayout.NORTH);
		
		try {
			
			socket = new DatagramSocket();
			address = InetAddress.getByName("localhost");
			sendPacket = new DatagramPacket(buf, buf.length, address, 8000);
			receivePacket = new DatagramPacket(buf, buf.length);
			System.out.println("connected");
		    
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		
		start = new Start();
		start.addMouseListener(new ClickListener());
		start.addMouseMotionListener(new MoveListener());
		this.add(start, BorderLayout.CENTER);
		setSize(725, 580);
		setResizable(false);
		setDefaultCloseOperation(3);
		setLocationRelativeTo((Component)null);
		setVisible(true);
		start.action();
		
	}
	
	public void displayPlayer(Map<Integer,Integer> receiveMap) {
		String display = "";
		Iterator<Map.Entry<Integer, Integer>> it = receiveMap.entrySet().iterator();
        while(it.hasNext()) {
      	  Map.Entry<Integer, Integer> entry = it.next();
      	  Integer key = entry.getKey();
      	  Integer val = entry.getValue();
      	  if(key == 1)
      		  continue;
      	  else {
      		  String eachPlayer = "Player " + key + ": " + val + "; ";
      		  display  = display + eachPlayer;
      	  }
        }
        if(start.gameover) {
        	label.setText("");
        	label1.setText("             THE BEST SCORE IS " + receiveMap.get(0) + "  ||  YOUR SCORE IS " + start.score);
        }
        else if(!start.gameover) {
        	label.setText("ThisPlayer: " + start.score);
        	label1.setText(display);
        }
        
	}
	
	private class ClickListener implements MouseListener{

		@Override
		public void mouseClicked(MouseEvent e) {
			// Initialize buffer for each iteration
			Arrays.fill(buf, (byte)0);
			
			Point p = e.getPoint();
			if(start.catched && p.x >=361  &&p.x <= 418 && p.y <= 215 && p.y >= 177) {
				
				try {
					start.score++;
					start.clickBasket = true;
					// send score to the server in a packet
					
					Map<Integer, Integer> sendMap = new LinkedHashMap<Integer, Integer>();
					if(!start.gameover) {
						if(sendMap.containsKey(0)) {
							sendMap.remove(0);
						}
						if(!sendMap.containsKey(1))
							sendMap.put(1, start.score);
						else
							sendMap.replace(1, start.score);
					}
					else {
						if(sendMap.containsKey(1)) {
							sendMap.remove(1);
						}
						if(sendMap.containsKey(0))
							sendMap.replace(0, start.score);
						else
							sendMap.put(0, start.score);
					}
					
					baos= new ByteArrayOutputStream();
			        oos = new ObjectOutputStream(baos);
			        oos.writeObject(sendMap);
			        oos.flush();
					
			        sendPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, InetAddress.getByName("localhost"), 8000);
			        socket.send(sendPacket);
					
			    
			        // receive scores from the server in a packet
			        socket.receive(receivePacket);
			        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
			        ObjectInputStream ois = new ObjectInputStream(bais);
			        Map<Integer,Integer> receiveMap = new LinkedHashMap<Integer, Integer>();
			        receiveMap = (LinkedHashMap)ois.readObject();
			        
				} catch (IOException | ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}			
			
		}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {}

		@Override
		public void mouseExited(MouseEvent e) {}
		
	}
	
	public class MoveListener implements MouseMotionListener{

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			// send score to the server in a packet
			try {		
				Map<Integer, Integer> sendMap = new LinkedHashMap<Integer, Integer>();
				if(!start.gameover) {
					if(sendMap.containsKey(0)) {
						sendMap.remove(0);
					}
					if(!sendMap.containsKey(1))
						sendMap.put(1, start.score);
					else
						sendMap.replace(1, start.score);
				}
				else {
					if(sendMap.containsKey(1)) {
						sendMap.remove(1);
					}
					if(sendMap.containsKey(0))
						sendMap.replace(0, start.score);
					else
						sendMap.put(0, start.score);
				}
				
					baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(sendMap);
					oos.flush();
					
			        sendPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, InetAddress.getByName("localhost"), 8000);
			        socket.send(sendPacket);
					
					
					// receive scores from the server in a packet
					socket.receive(receivePacket);
					ByteArrayInputStream bais = new ByteArrayInputStream(buf);
					ObjectInputStream ois = new ObjectInputStream(bais);
					Map<Integer,Integer> receiveMap = new LinkedHashMap<Integer, Integer>();
					receiveMap = (LinkedHashMap)ois.readObject();
				
					//display to the screen
					displayPlayer(receiveMap);
					
			} catch (IOException | ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}
		
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
	    new Player();
	}
	
	
	
	

}
