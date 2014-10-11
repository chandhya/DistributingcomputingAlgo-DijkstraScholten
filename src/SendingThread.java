import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;






public class SendingThread extends Thread implements Runnable{
	
	Socket socket;
	MessageType1 mt;
	ObjectOutputStream oos;
	Message m1;
	SendingThread(Socket receiver, MessageType1 mt)
	{
		this.socket =receiver;
		
		this.mt=mt;
	}
	
	@Override
	public void run()
	{
		
			try {
			InetSocketAddress rcpt = new InetSocketAddress(socket.getInetAddress().getHostName(),Node.SERVER_PORT);
			
			if(mt.equals(MessageType1.ACK))
			{
				Thread.sleep(2000);
			 m1 = new Message(1234,rcpt,Node.currentNode,MessageType1.ACK,Node.getAckCount());
			 System.out.println("Ack message sent"); 
			 Node.out.println("Ack message sent");
			// oos.wait(); 
			 oos = Node.outMap.get(Node.getNodeNumber(socket));
			 synchronized(oos){
				 
			 oos.writeObject(m1);
			 oos.flush();
			 }
			
			}
			else if(mt.equals(MessageType1.COMPUTATION))
			 {Node.tick();Node.ackCountIncrement();long timeStamp = Node.getTime();
			 String timeStr = Node.convertSecondsToHMmSs(timeStamp);
			 if(!Node.isTerminated){
			 	System.out.println("Computation Message sent to Node(Recepient): "+Node.getNodeNumber(socket));
				System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
				System.out.println("Acknowledgements to be received: "+Node.getAckCount());
				Node.out.println("Computation Message sent to Node(Recepient): "+Node.getNodeNumber(socket));
				Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
				Node.out.println("Acknowledgements to be received: "+Node.getAckCount());
			 }
				Node.compCountIncrement();
				m1 = new Message(timeStamp,rcpt,Node.currentNode,MessageType1.COMPUTATION,Node.getAckCount());
				oos = Node.outMap.get(Node.getNodeNumber(socket));
				synchronized(oos){
				
				oos.writeObject(m1);
				oos.flush();
				}
				//oos.notify();
							 }
			else if(mt.equals(MessageType1.TERMINATE)){
				Node.tick();Node.ackCountIncrement();long timeStamp = Node.getTime();
				String timeStr = Node.convertSecondsToHMmSs(timeStamp);
				 
				if(Node.currentNode.getHostName().equals("dc01.utdallas.edu"));
				{System.out.println("Termination message sent");
				 
				 System.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
				 Node.out.println("Termination message sent");
				 Node.out.println("Physical Clock value now: "+timeStamp+"ms ("+timeStr+")");
				}
					m1 = new Message(timeStamp,rcpt,Node.currentNode,MessageType1.TERMINATE,Node.getAckCount());
					oos = Node.outMap.get(Node.getNodeNumber(socket));
					oos.writeObject(m1);
					oos.flush();
				
			}
			//oos= new ObjectOutputStream(socket.getOutputStream());
			
			
			
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}


