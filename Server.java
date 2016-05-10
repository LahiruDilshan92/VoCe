import java.net.* ;
import java.io.*;
import javax.sound.sampled.*;

public class Server extends Thread{
	private final static int packetsize = 1024 ;
	SourceDataLine sourceDataLine;
	AudioFormat audioFormat;
	int port = 2000;
	static byte tempBuffer[] = new byte[500];
	InetAddress address;
	
	public AudioFormat getAudioFormat() {
    	float sampleRate = 16000.0F;
    	int sampleSizeInBits = 16;
    	int channels = 2;
    	boolean signed = true;
    	boolean bigEndian = true;
    	return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

/* This method initialize the audio format and maximum volume for the server part */
	public void getServerAudio(){
			try{
				this.audioFormat = this.getAudioFormat();
				DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, this.audioFormat);
	        	this.sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo1);
	        	this.sourceDataLine.open(this.audioFormat);
	        	this.sourceDataLine.start();

	        	//Setting the maximum volume
	        	FloatControl control = (FloatControl)this.sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
	        	control.setValue(control.getMaximum());
	        }	
        	catch(Exception e){
				e.printStackTrace();
			}	
	}


public void run(){
		try{
			
				App aa = new App();
				String multicastIP = aa.str;
				address=InetAddress.getByName(multicastIP);
				MulticastSocket socket=new MulticastSocket(port);
				System.out.println("The server is ready...") ;
				socket.joinGroup(address);

				DatagramPacket packet = new DatagramPacket( new byte[packetsize], packetsize );
				this.getServerAudio();
				int prev=0,current=0,order,loss;

			for(;;){
				socket.receive(packet);
				tempBuffer = packet.getData();
				

				ByteArrayInputStream byteStream = new ByteArrayInputStream(tempBuffer);
				ObjectInputStream iStream = new ObjectInputStream(byteStream);
				try{
					Message msg = (Message) iStream.readObject();

				/*	Check the statistics*/
					order=msg.checkOrder(prev);
					loss=msg.checkPacketLoss(current);
					msg.statistics(current,order,loss);
					current++;
					
					prev=msg.getVal();
					iStream.close();
					this.sourceDataLine.write(msg.buf, 0, 500);
				} catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
				
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
}

}
