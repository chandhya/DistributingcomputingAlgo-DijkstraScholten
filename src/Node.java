/*@author Chandhya
 * Node class is parent class all calls both client and server threads
 * 
 * */
import java.io.File;
import java.io.FileNotFoundException;


import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
public class Node {
	public static final int SERVER_PORT = 1922;
	static InetSocketAddress currentNode;
	static Socket[] nodes =new Socket[4];
	static Socket[] serverSockets = new Socket[4];
	static ObjectInputStream[] peerInput = new ObjectInputStream[4];
	public static ConcurrentHashMap<Integer, ObjectOutputStream> outMap = new ConcurrentHashMap<Integer, ObjectOutputStream>();
	public static InetSocketAddress[] PEERS= new InetSocketAddress[4];
	public static Socket parent;
	public static PrintWriter out;
	public static int ackCount; //pending acks in this node
	public static int compCount;
	public static boolean isActive;
	public static volatile boolean hasTerminated;
	public static volatile boolean isTerminated;
	private static long time ;
	public static int v;           
	public static int j;
	public static int nodeNo;
	static int count=0;
	public static double[] vFromFile = new double[7];
	public static Integer[] jFromFile = new Integer[7];
	public static CopyOnWriteArrayList<Socket> parentNodes = new CopyOnWriteArrayList<Socket>();
	public static FileWriter fstream;
	//public static BufferedWriter out ;
	public static CopyOnWriteArrayList<Socket> connectedNodes = new CopyOnWriteArrayList<Socket>();
	//public static CopyOnWriteArrayList Socket[] connectedNodesV = connectedNodes;
	public static int NO_OF_NODES =4;
	public static String FILE_NAME;
	static int index=0;
	public static void main(String[] args) throws IOException, InterruptedException {
						
			currentNode = new InetSocketAddress(InetAddress.getLocalHost().getHostName(),
					SERVER_PORT);
				nodeNo = Integer.parseInt((currentNode.getHostName().substring(2,4)));
				System.out.println("Node no is-"+nodeNo);
				FILE_NAME = "node_"+nodeNo+".txt";
				File file = new File (FILE_NAME);
				out = new PrintWriter(file);
				out.write("Test write");
				
					initialiseNetwork();
				checkTermination();
		}

	 public static void checkTermination() {
		
		 
		 while(true)
		 {
		 if(isTerminated)
		{
			System.out.println("Surely gets here");
			
			while(true)
			{
						
			if(Node.getAckCount()==0)
			{
				
			for(int y=0;y<Node.connectedNodes.size();y++)
			{
				Socket parent = Node.connectedNodes.get(y);
				Thread st4 = new SendingThread(parent,MessageType1.TERMINATE);
				st4.start();
			
			}
			break;
			}
			else{
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				while(Node.getAckCount()>0)
				{
					Node.ackCountDecrement();
					System.out.println("Pending ACK received");
					System.out.println("ACKs yet to be received:" +Node.getAckCount());
					Node.out.println("Pending ACK received");
					Node.out.println("ACKs yet to be received:" +Node.getAckCount());
				}
			}
			}
			Node.tick();
			long timeStamp = Node.getTime();
			String timeStr = Node.convertSecondsToHMmSs(timeStamp);
			try {
				
				Thread.sleep(6000);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Pending ACK sent");
			System.out.println("ACKs yet to be received: 0");
			System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
			System.out.println("Computation Terminated finally");
			Node.out.println("Pending ACK sent");
			Node.out.println("ACKs yet to be received: 0");
			Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
			Node.out.println("Computation Terminated finally");
			break;
			
			
			
			
		}
	}
		
	}

	static double generateRandvalue(double vMin, double vMax) {
	
		
		//while(count<25)
		//{
		double rand = Math.random();
		double range = vMax-vMin;
		double adjustment = range* rand;
		double result = vMin+adjustment;
		//
		
		return result;
	//	}
		
		
	}

	private static void fileRead() {
		try {
			File text = new File(FILE_NAME);
			
		    Scanner scnr = new Scanner(text);
		    while(true){
		    	if(scnr.hasNextDouble())
		    	{vFromFile[index] = scnr.nextDouble();}
		    	else break;
		    	if(scnr.hasNextInt()){
		    		jFromFile[index] = scnr.nextInt();}
		    	else break;
		    	
	            index++;
	        }  
		  scnr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static void initialiseNetwork() {
		try {
			
			
			currentNode = new InetSocketAddress(InetAddress.getLocalHost().getHostName(),
					SERVER_PORT);
			long timeStamp = Node.getTime();
			String timeStr = Node.convertSecondsToHMmSs(timeStamp);
			
			System.out.println("Initial wait time -: "+timeStr);
			
			Thread t1 = new Server();
			Thread t2 = new Client();
			t1.start();
			
			if(currentNode.getHostName().equals("dc01.utdallas.edu"))
			{
				Node.setStatus(true);
				t2.start();
			}
			else
			{
				//System.out.println("waiting for node to become active!");
				Node.setStatus(false);
				while(true){
				if(Node.getStatus())
				
					{
						//System.out.println("Node is activated!");
						t2.start();
					break;
					}
				
				}
				
				
			}
			//t1.join();
			//t2.join();
		//	new ListeningThread();
			
			
			
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	/*
	public static synchronized void setValue(int index, Socket value) {
	    synchronized (connectedNodes[index]) {
	    	
	    	
	    		connectedNodes[index] = value;
	    	
	    }
	    
	   // System.out.println("Values being set after synch:");
	    //for(int y=0;y<connectedNodes.length;y++)
	    //{
	    	//System.out.println("Connected to - "+connectedNodes[y]);
	    //}
	  }
	/*
	public static synchronized Socket getValue(int index) {
		// System.out.println("Values being set:");
		  //  for(int y=0;y<connectedNodes.length;y++)
		    //{
		    	//System.out.println("Connected to - "+connectedNodes[y]);
		    //}
	    synchronized (connectedNodes) {
	      return connectedNodes[index];
	    }
	    
	  }*/
	 static int getNodeNumber(Socket connectedNode) {
		if(connectedNode!=null)
		{
		int x = Integer.parseInt(Character.toString(connectedNode.getInetAddress().getHostName().charAt(3)));
		
		return x;
		}
		return 0;
	}
	 public static String convertSecondsToHMmSs(long seconds) {
		    long s = seconds % 60;
		    long m = (seconds / 60) % 60;
		    long h = (seconds / (60 * 60)) % 24;
		    return String.format("%d:%02d:%02d", h,m,s);
		}
	 static  synchronized void ackCountIncrement()
	{
		ackCount++;
	}
	 static  synchronized void ackCountDecrement()
		{
		if(ackCount!=0)
		 ackCount--;
		}
	 static  synchronized void compCountIncrement()
		{
		 compCount++;
		}
		 static  synchronized void compCountDecrement()
			{
			 compCount--;
			}
		 static int getCompCount() {
				return compCount;
			}

			static void setCompCount(int compCountToSet) {
				compCount = compCountToSet;
			}
	 static int getAckCount() {
			return ackCount;
		}

		static void setAckCount(int ackCounttoSet) {
			ackCount = ackCounttoSet;
		}
		 static synchronized boolean getStatus() {
				return isActive;
			}

			static  synchronized void setStatus(boolean isActiveFlag) {
				isActive = isActiveFlag;
			}
			static synchronized void setParent(Socket parentValue){
				parent = parentValue;
				}
			static synchronized Socket getParent(){
				return parent;
				}
		static synchronized void tick() {
			time = System.currentTimeMillis();
			time++;
		}

		static long getTime() {
			return time;
		}

		public static void initiateTermination() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(Node.getAckCount()==0)
			{
				System.out.println("--------TERMINATION!!--------");
			for(int y=0;y<Node.connectedNodes.size();y++)
			{
				
			Socket parent = Node.connectedNodes.get(y);
			Thread st4 = new SendingThread(parent,MessageType1.TERMINATE);
			st4.start();
			}
			}
			
		}
}
