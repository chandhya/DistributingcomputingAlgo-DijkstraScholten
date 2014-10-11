import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
/*@author Chandhya
 * Server class listens for requests and spawns listening threads.
 * 
 * */

public class Server extends Thread{
	
    Socket socket;
    ObjectInputStream iis;
    ObjectOutputStream oos;
    int i;
    boolean isPresent;
    @Override
	public void run() 
    {
    	ServerSocket serverSocket=null;
		try {
			serverSocket = new ServerSocket(Node.SERVER_PORT);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	while(true) {
    		
    		 try {
				
				
				socket = serverSocket.accept();
				//Node.setStatus(true);
				//System.out.println("Initiation accepted! Machine I'm connected to is"+socket.getInetAddress().getHostName());
				Node.tick();
				long timeStamp = Node.getTime();
				String timeStr = Node.convertSecondsToHMmSs(timeStamp);
				
				
				
					iis = new ObjectInputStream(socket.getInputStream());
				Message m = (Message) iis.readObject();
				oos = new ObjectOutputStream(socket.getOutputStream());
				Node.outMap.put(Node.getNodeNumber(socket), oos);
				System.out.println("Computation Message received from Node(Sender): "+Node.getNodeNumber(socket));
				System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
				//System.out.println("Node joins tree with this request");
				Node.out.println("Computation Message received from Node(Sender): "+Node.getNodeNumber(socket));
				Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
				//Node.out.println("Node joins tree with this request");
				if(m.mt.equals(MessageType1.INITIATE))
				{
					System.out.println("it gets here");
					/*for(int y=0;y<Node.connectedNodes.size();y++)
					{
						System.out.println("Connected node content -"+Node.connectedNodes.get(y));
					}*/
					if(Node.connectedNodes.isEmpty())
					{
						Node.setStatus(true);
						
						System.out.println("Node joins tree with this request");
						Node.out.println("Node joins tree with this request");
						Node.setParent(socket);
						Node.parentNodes.add(socket);
						Node.connectedNodes.add(socket);
						
					}
					else if(!Node.getStatus()){
						if(Node.hasTerminated){
							System.out.println("Msgs in transit during termination");
							Node.out.println("Msgs in transit during termination");
							Node.setStatus(false);
							Thread st = new SendingThread(socket,MessageType1.ACK);
							st.start();
							 }
						else{
						
						Thread.sleep(2000);
						Node.setParent(socket);
						
						System.out.println("From Idle to Active upon receipt of message");
						Node.setStatus(true);
						
						Node.parentNodes.add(socket);
						Node.connectedNodes.add(socket);}
					}
					else
					
					{
						//Node.setStatus(true);
						Node.connectedNodes.add(socket);
						System.out.println("Node will send ACK in reponse to: "+Node.getNodeNumber(socket));
						System.out.println("Intended for- "+Node.getNodeNumber(socket));
						Thread st = new SendingThread(socket,MessageType1.ACK);
						st.start();
					}
					//System.out.println("Initiation accepted! Machine I'm connected to is"+socket.getInetAddress().getHostName());
					//oos = new ObjectOutputStream(socket.getOutputStream());
					
				//	Node.peerOutput.put(Node.getNodeNumber(socket),oos);
					//System.out.println("Output stream added - "+Node.getNodeNumber(socket));
					//oos.writeObject(new Message(timeStamp,new InetSocketAddress(socket.getInetAddress().getHostName(),Node.SERVER_PORT),Node.currentNode,MessageType1.INITIATE,Node.getAckCount()));
					
				}	
				//else{
					//System.out.println("the check worked but stream is null");
				//	}
				//}
				
				Thread.sleep(2000);
				Thread lt2 = new ListeningThread(socket,iis,true);
				lt2.start();
				
				
				
				
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		 /*
    		 if(Node.compCount>10){
    			 try {
					Thread.sleep(30000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    			 //break;
    		 }*/
    	}/*
    	for(int z=0;z<Node.connectedNodes.size();z++){
    		if(Node.connectedNodes!=null)
			System.out.println("I'm connected with :"+Node.connectedNodes.get(z).getInetAddress().getHostName());
		}*/
    	/*
    	try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }

}
