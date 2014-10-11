import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map.Entry;
/*@author Chandhya
 * 
 * Listening Thread constantly listens to messages from connected nodes
 * */

public class ListeningThread extends Thread implements Runnable
{
	
	Socket socket=null;
	ObjectInputStream ois;
	ObjectOutputStream oos;
	Message m;
	public ListeningThread(Socket socket, ObjectInputStream iis, boolean isCreated)
	 {
		this.socket = socket;
		try {
			if(!isCreated)
			this.ois =new ObjectInputStream(socket.getInputStream());
			else 
			this.ois = iis;
		}
			catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void run()
	{	/*
		try {
			ois = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}*/
		
	while(true)
	{
		if(socket!=null)
		{
			try {
		
				
			//	if(ois.readObject()!=null)
				//{
				//synchronized (ois) {
				m = (Message)ois.readObject();
				//}
			//	System.out.println("even if there is nothing in stream,it executes fine!");
				
			//	if(m.mt.equals(MessageType1.INITIATE))
				//{}
				if(m.mt.equals(MessageType1.COMPUTATION))
				{
					Node.tick();
					long timeStamp = Node.getTime();
					String timeStr = Node.convertSecondsToHMmSs(timeStamp);
					if(!Node.isTerminated){
					System.out.println("Computation Message received from Node(Sender): "+Node.getNodeNumber(socket));
					System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
					System.out.println("Node will send ACK in reponse to: "+Node.getNodeNumber(socket));
					Node.out.println("Computation Message received from Node(Sender): "+Node.getNodeNumber(socket));
					Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
					Node.out.println("Node will send ACK in reponse to: "+Node.getNodeNumber(socket));
					}
					//oos =Node.peerOutput.get(Node.getNodeNumber(socket));
					if(Node.getStatus())
					{
					System.out.println("Intended for- "+Node.getNodeNumber(socket));
					Thread st = new SendingThread(socket,MessageType1.ACK);
					st.start();
					}
					else if(!Node.getStatus())
					{
						if(Node.hasTerminated)
						{ System.out.println("Msgs in transit during termination");
							Node.setStatus(false);
							Thread st = new SendingThread(socket,MessageType1.ACK);
							st.start();
							 }
						
						else{
						Thread.sleep(2000);
						Node.setStatus(true);
						Node.setParent(socket);
						Node.parentNodes.add(socket);
						if(!Node.isTerminated)
						{
						System.out.println("From Idle to Active upon receipt of message");
						Node.out.println("From Idle to Active upon receipt of message");
						}
						}
					}
					
				}
				else if(m.mt.equals(MessageType1.ACK))
				{
					Node.tick();
					long timeStamp = Node.getTime();
					String timeStr = Node.convertSecondsToHMmSs(timeStamp);
					//if(Node.getAckCount()!=0)
					//{
					Node.ackCountDecrement();
					if(!Node.isTerminated)
					{
					//System.out.println("I am Process -" +Node.currentNode.getHostName());
					System.out.println("ACK Message received from Node(Sender): "+Node.getNodeNumber(socket));
					System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
					System.out.println("ACKs yet to be received:" +Node.getAckCount());
					Node.out.println("ACK Message received from Node(Sender): "+Node.getNodeNumber(socket));
					Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
					Node.out.println("ACKs yet to be received:" +Node.getAckCount());
					}
					if(Node.currentNode.getHostName().equals("dc01.utdallas.edu"))
					{
						System.out.println("guess I'm here");
						if(Node.getAckCount()==0)
						{
							Node.initiateTermination();
						}
						
					}
					//}
				}
				else if(m.mt.equals(MessageType1.TERMINATE)){
					Node.tick();
					long timeStamp = Node.getTime();
					String timeStr = Node.convertSecondsToHMmSs(timeStamp);
					Node.hasTerminated =true;
					Node.isTerminated =true;
					System.out.println("ACK sent");
					System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
					System.out.println("Computation Terminated");
					Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
					System.out.println("ACK yet to received: 0");
					Node.out.println("ACK yet to received: 0");
					Node.out.println("Computation Terminated");
					Node.out.close();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
			//	e.printStackTrace();
				break;
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				break;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			
		}
	}
	}
	}