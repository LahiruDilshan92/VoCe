import java.net.* ;
import java.io.*;
import javax.sound.sampled.*;
import java.nio.*;
import java.util.*;

public class Message implements Serializable{

	public byte buf[] = new byte[1024];
	public int val;

//Define the UDP packet format
	public Message(byte buf[], int val){
		this.buf = buf;
		this.val = val;
	}

	public int getVal(){
		return this.val;
	}

/*Print the statistics of the application every 7500 packets*/
	public void statistics(int statis,int a,int b){
		if(statis % 7500 ==0 && statis>1){
			System.out.println(a+" Packets are out of order ");
			System.out.println(b+" Packets are lost ");
		}

	}

/* Check that the packets are out of order*/
	public int checkOrder(int prev){
		int num=0;
		int discard=0;
		if(prev+1!=this.val){
			num++;
			discardPacket(prev+1);
		}
		return num;							
	}

/* Check that the packets are lost */
	public int checkPacketLoss(int current){
		int num=0;
		if(current!=this.val){
			num++;
		}
		return num;
	}	

/* Get the sequence number of the packet that is going to discard 	*/
	public int discardPacket(int discard){
		return discard;
	}

}