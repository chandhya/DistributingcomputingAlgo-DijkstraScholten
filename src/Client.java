import java.io.IOException;
import java.io.ObjectInputStream;

import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
/*@author Chandhya
 * 
 * Client class handles initial request send to join the tree and initiate connection with nodes
 * Also handles the checks for v and j values
 * */

public class Client extends Thread {
	Socket clientSocket;
	ObjectOutputStream out ;
	ObjectInputStream iis ;
	boolean isAlreadyConnected =false;
	String jString;
	Socket receiver;
	int i=0;
	double v;
	int jvalue;
	@Override
	public void run(){
		while(true)
		{
			if(Node.compCount<=20 )
			{
			
			v = Node.generateRandvalue(0.01, 1);
			if(Node.currentNode.getHostName().equals("dc01.utdallas.edu"))
				{
				v = Node.generateRandvalue(0.1, 1);
				/*try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				}
			while(true){
				jvalue = (int)Node.generateRandvalue(1.0, 18.0);
				if(jvalue != Node.nodeNo)
					break; 
			}
			if(jvalue<10)
			{
				jString = "0"+jvalue;
			}
			else{
				jString = String.valueOf(jvalue);
			}
			if(jvalue==9){jString = String.valueOf(11);}
			if (jvalue==10){jString = String.valueOf(12);}
			
			
			if(Node.getStatus() && v>=0.1)// && Node.compCount<=10)
			{
				
			for(int j=0;j<Node.connectedNodes.size();j++)
			{
				
			
				if(!Node.connectedNodes.isEmpty() &&jvalue==Node.getNodeNumber(Node.connectedNodes.get(j)))
				{
					
					receiver =Node.connectedNodes.get(j);
					isAlreadyConnected =true;
					break;
					//System.out.println("Value of j from file - "+Node.jFromFile[i]);
					//System.out.println("flag approriately set!");
				}else {isAlreadyConnected =false;}
				
			}
				
				if(!isAlreadyConnected )	
				{
				while (true) {
						try {
							//clientSocket = new Socket("dc0" + Node.jFromFile[i] + ".utdallas.edu",Node.SERVER_PORT);
							
							clientSocket = new Socket("dc" +jString + ".utdallas.edu",Node.SERVER_PORT);
							if (clientSocket == null) 
							{
							Thread.sleep(1000);
							} else 
							{
						//	System.out.println("Client made connection with"+clientsocket.getInetAddress().getHostName());
							break;
							}
							} 
							catch (Exception e) {
							//e.printStackTrace();
							}}
							if(clientSocket!=null)
							{
								//System.out.println("Guess not!");
							try {
								Node.connectedNodes.add(clientSocket);
									
								//	for(int t=0;t<Node.connectedNodes.size();t++)
									//{
										//System.out.println("Node from connectedNodes:"+Node.connectedNodes.get(t).getInetAddress().getHostName());
									//}
								out = new ObjectOutputStream(clientSocket.getOutputStream());
								Node.outMap.put(Node.getNodeNumber(clientSocket),out);
								//System.out.println("Output stream added - "+Node.getNodeNumber(clientSocket));
								InetSocketAddress receipient = new InetSocketAddress("dc0" + Node.jFromFile[i] + ".utdallas.edu",Node.SERVER_PORT);
								//Node.isActive =true;
								Node.tick();
								Node.compCountIncrement();
								Node.ackCountIncrement();//increment physical time
								long timeStamp = Node.getTime();
								String timeStr = Node.convertSecondsToHMmSs(timeStamp);
								Message m = new Message(timeStamp,receipient,Node.currentNode,MessageType1.INITIATE,Node.getAckCount());
								
								out.writeObject(m);
								
								if(!Node.isTerminated){
								System.out.println("Computation Message sent to Node(Recepient): "+Node.getNodeNumber(clientSocket));
								
								System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
								System.out.println("Acknowledgements to be received: "+Node.getAckCount());
								
								Node.out.println("Computation Message sent to Node(Recepient): "+Node.getNodeNumber(clientSocket));
								Node.out.println("Physical Clock value now: "+timeStamp);
								Node.out.println("Acknowledgements to be received: "+Node.getAckCount());
								}
								//System.out.println("Node - "+Node.currentNode.getHostName()+"has connected to "+clientSocket.getInetAddress().getHostName());
								
								/*for(int k=1;k<=4;k++) // This loop is created in order to place node connected in the approp
								{
									if(k==Node.getNodeNumber(clientSocket))
									{
										
										Node.parentNodes.add(clientSocket);
									//	Node.setValue(k-1, clientSocket);
									//System.out.println("Successfully inserted");
									}
								}*/
								
								//if(clientSocket!=null && clientSocket.getInputStream()!=null)
								//{
								//iis = new ObjectInputStream(clientSocket.getInputStream());
								//Message m2 = (Message) iis.readObject();
								//if(m2.mt.equals(MessageType1.INITIATE))
								//{
									Thread.sleep(2000);
								
								ListeningThread lt = new ListeningThread(clientSocket,null,false);//listening (Say ex :for msgs from 4 to 3,
																						//if 3 is current node and 3 has requested and setup a connection with 4)
								lt.start();
								//}
								//}
								
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							 //Not sure if this is required
							catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							}
				}
			else{
				//System.out.println("Recepient is"+receiver.getInetAddress().getHostName());
				Thread st2;
				st2 = new SendingThread(receiver,MessageType1.COMPUTATION);
				st2.start();
				
			}
			}	
			else
			{
				if(Node.getStatus())
				{
				/*	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	*/
				Node.setStatus(false);
				Socket parent = Node.getParent();
				if(!Node.isTerminated){
				System.out.println("From Active to Idle");
				System.out.println("Sending ACK to parent and detaching from tree");
				Node.out.println("From Active to Idle");
				Node.out.println("Sending ACK to parent and detaching from tree");
				
				}
				synchronized(parent){
				Thread st3 = new SendingThread(parent,MessageType1.ACK);
				st3.start();}
				/*for(int y=0;y<Node.parentNodes.size();y++)
				{
				Socket parent = Node.parentNodes.get(y);
				Thread st3 = new SendingThread(parent,MessageType1.ACK);
				st3.start();
				
				}*/
				}
				
			}
				if(Node.currentNode.getHostName().equals("dc01.utdallas.edu"))
				{
					if(Node.getAckCount()==0)
						{System.out.println("Termination initiated auto!");
					for(int y=0;y<Node.connectedNodes.size();y++)
					{
						
					Socket value = Node.connectedNodes.get(y);
					Thread st4 = new SendingThread(value,MessageType1.TERMINATE);
					st4.start();
					
					}
					try {
						Thread.sleep(2000);
						break;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					}
					
				}
				
			
			
			}
			
			//large else clause (runs after required no of computations)
			else{
				if(Node.getStatus() && !(Node.currentNode.getHostName().equals("dc01.utdallas.edu")))
				{
					Node.hasTerminated =true;
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				
				Node.setStatus(false);
				if(!Node.isTerminated){
				System.out.println("From Active to Idle");
				System.out.println("Sending ACK to parent and detaching from tree");
				Node.out.println("From Active to Idle");
				Node.out.println("Sending ACK to parent and detaching from tree");
				}
				Socket parent = Node.getParent();
				synchronized(parent){
				Thread st3 = new SendingThread(parent,MessageType1.ACK);
				st3.start();}
				
				System.out.println("Ack message sent"); 
				 Node.out.println("Ack message sent");
				try {
					Thread.sleep(12000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
				/*for(int y=0;y<Node.parentNodes.size();y++)
				{
				Socket parent = Node.parentNodes.get(y);
				Thread st3 = new SendingThread(parent,MessageType1.ACK);
				st3.start();
				
				}*/
				}
				if(Node.currentNode.getHostName().equals("dc01.utdallas.edu"))
				{
					//Node.setStatus(false);
					
					while(true)
					{
						try
						{
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	//System.out.println("Does it get here??");
						
					if(Node.getAckCount()==0)
					{
						//System.out.println("Termination initiated due to count - Sending from client");
					for(int y=0;y<Node.connectedNodes.size();y++)
					{
						Socket parent = Node.connectedNodes.get(y);
						Thread st4 = new SendingThread(parent,MessageType1.TERMINATE);
						st4.start();
					/*try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				
					*/
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
							System.out.println("ACKs yet to be received:" +Node.getAckCount());
						}
					}
					}
					try {
						Thread.sleep(20000);
						break;
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
				
			}
			/*
			if(Node.compCount>15) {
				try {
					System.out.println("Termination initiated!");
					Thread.sleep(30000);
					break;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//}
			
			} */
			
		}
		
	
	}
	
}

			
			
		
				
				
		
		
	/*	for(int z=0;z<Node.connectedNodes.size();z++){
			if(Node.connectedNodes!=null)
			System.out.println("I'm connected with :"+Node.connectedNodes.get(z).getInetAddress().getHostName());
		}
		try {
			
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	
	
	

