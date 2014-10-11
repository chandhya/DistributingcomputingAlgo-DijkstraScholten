
/*@author chandhya
 * The serialize message class has all fields required for message transfer
 * 
 * */
import java.io.Serializable;
import java.net.InetSocketAddress;





public class Message implements Serializable{
	
	long timeStamp; // Physical clock
	InetSocketAddress recevierAddr;
	InetSocketAddress sendAddr;// Senders address
	MessageType1 mt; 
	int ackCount ;
	public Message(long timeStamp, InetSocketAddress recevierAddr, InetSocketAddress sendAddr,
			MessageType1 mt,int ackCount) {
		
		this.timeStamp = timeStamp;
		this.recevierAddr = recevierAddr;
		this.sendAddr = sendAddr;
		this.mt = mt;
		this.ackCount =ackCount;
	}

}
