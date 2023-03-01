package FishingGame;

import java.awt.BorderLayout;
import java.sql.*;
import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.*;

public class Server extends JFrame{
	// Text area for displaying contents
	private JTextArea jta = new JTextArea();
	private int count = 0;
	
	ByteArrayOutputStream baos;
    ObjectOutputStream oos;
  
	//The byte array for sending and receiving datagram packets
	private byte[] buf = new byte[256];
  
	public Server() {
		// Place text area on the frame
	    setLayout(new BorderLayout());
	    add(new JScrollPane(jta), BorderLayout.CENTER);

	    setTitle("DatagramServer");
	    setSize(500, 300);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setVisible(true);
	    
	    Map<Integer, Integer> scores = new LinkedHashMap<Integer, Integer>();
	    
	    jta.append(scores + "\n");
	    // Create a packet for receiving data
	        DatagramPacket receivePacket =
	          new DatagramPacket(buf, buf.length);
	    try {
	        // Create a server socket
	        DatagramSocket socket = new DatagramSocket(8000);
	      
	        // Create a packet for sending data
	        DatagramPacket sendPacket = new DatagramPacket(buf, buf.length);

	        while (true) {
	        	
	        	
	        	 // Initialize buffer for each iteration
	        	Arrays.fill(buf, (byte)0);
	        	baos= new ByteArrayOutputStream();
		        oos = new ObjectOutputStream(baos);
	        	// Receive radius from the client in a packet
		        socket.receive(receivePacket);
		        ByteArrayInputStream bais=new ByteArrayInputStream(receivePacket.getData());
		        ObjectInputStream ois = new ObjectInputStream(bais);
		        Map<Integer,Integer> receiveMap = new LinkedHashMap<Integer, Integer>();
		        receiveMap = (LinkedHashMap)ois.readObject();
			  
		        int port = receivePacket.getPort();
		        int score = -1;
		        jta.append("\n" + "receivedHashMap is " + receiveMap + '\n');
		        if(receiveMap.containsKey(0)) {
		        	score = receiveMap.get(0);
		        	int key1 = 0;
		        	int highest = getRecord();
		        	if(!scores.containsKey(key1)) {
		        		scores.put(key1, highest);
		        	}
		        	else {
		        		scores.replace(key1, highest);
		        	}
		        }
		        else if(receiveMap.containsKey(1)) {
		        	if(scores.containsKey(0))
		        		scores.remove(0);
		        	score = receiveMap.get(1);
		        	insertRecord(port, score);
		        }
			  
		        // put scores in a hashMap	          
		        if(!scores.containsKey(port) && score >= 0) {
		        	scores.put(port, score);
		        }
		        else if(scores.containsKey(port) && score >= 0) {
		        	scores.replace(port, score);
		        }
	          
		        Iterator<Map.Entry<Integer, Integer>> it = scores.entrySet().iterator();
		        while(it.hasNext()) {
		        	Map.Entry<Integer, Integer> entry = it.next();
	        	  
		        	Integer key = entry.getKey();
		        	if(isAvailable(key)) {
		        		it.remove();
		        	}
	        	  
		        }
	          
		        jta.append("hashMap is " + scores + '\n');

		        // Send hashmap to the client in a packet
		        oos.writeObject(scores);
		        oos.flush();
		        sendPacket = new DatagramPacket(baos.toByteArray(), baos.toByteArray().length, receivePacket.getAddress(), receivePacket.getPort());
		        socket.send(sendPacket);
		        jta.append(getWarningString());
	        }
	    }
	    catch(IOException | ClassNotFoundException ex) {
	    	ex.printStackTrace();
	    }
	}
	
	public static boolean isAvailable(int port) {

		if(port == 0) {
			return false;
		}
	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }
	    return false;
	}
	
	public Connection connect() {
		Connection connection =null;
		try {
			if(connection == null) {
				Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection
			  ("jdbc:sqlite:fishing.db");
			return connection;
			}
		} catch (SQLException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);;
		}
		return null;
	}
	
	public void insertRecord(int portID, int score) {
		Connection connection = connect();
		String sql = "insert into record2(ID,portID,record) values(null,?,?);";
		if(connection != null) {
			try{
				//insert
				PreparedStatement pst = connection.prepareStatement(sql);
				pst.setInt(1, portID);
				pst.setInt(2, score);
				synchronized(pst) {
					pst.executeUpdate();
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.exit(0);
			}finally {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
			}
		}
	}
	
	public int getRecord() {
		Connection connection = connect();
		
		// Create a statement
	    Statement statement = null;
		try {
			statement = connection.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// Execute a statement
		ResultSet resultSet = null;
		if(connection != null) {
			//select
			try {
				resultSet = statement.executeQuery("select record from record2 order by record desc limit 1;");
				int record = resultSet.getInt("record");
				return record;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}finally {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.exit(0);
				}
			}
		}    
		return 0;
		
	}
	
	public static void main(String[] args) {
		new Server();
	}
}