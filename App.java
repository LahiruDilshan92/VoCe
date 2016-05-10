import java.io.*;
import javax.sound.sampled.*;
import java.net.* ;

public class App extends Thread{
	static byte buf[] = new byte[500];
	static int re_count;
	int port = 2000;
	int val = 1;
    static InetAddress address;
    static String str;

	AudioFormat audioFormat;
	TargetDataLine targetDataLine;	

	public AudioFormat getAudioFormat() {
    	float sampleRate = 16000.0F;
    	int sampleSizeInBits = 16;
    	int channels = 2;
    	boolean signed = true;
    	boolean bigEndian = true;
    	return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

/* This method is use to get available mixers and audio format */
	public void getClientAudio(){
		try{
			Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
	        	System.out.println("Available mixers:");
	        	Mixer mixer = null;
	        	for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
	            	System.out.println(cnt + " " + mixerInfo[cnt].getName());
	            	mixer = AudioSystem.getMixer(mixerInfo[cnt]);

	            	Line.Info[] lineInfos = mixer.getTargetLineInfo();
	            	if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
	                	System.out.println(cnt + " Mic is supported!");
	                	break;
	            	}
	        	}

	        	this.audioFormat = this.getAudioFormat();     //get the audio format
	        	DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, this.audioFormat);

	        	this.targetDataLine = (TargetDataLine) mixer.getLine(dataLineInfo);
	        	this.targetDataLine.open(this.audioFormat);
	        	this.targetDataLine.start();
	        }
	        catch(Exception e){
				e.printStackTrace();

	        }
	}

public void run(){
		try{
			String multicastIP=str;
			MulticastSocket socket = new MulticastSocket();
			address=InetAddress.getByName(multicastIP);
			System.out.println( "The App is ready..." ) ;
			this.getClientAudio();
			
			while (true) {
                re_count = this.targetDataLine.read(buf, 0, buf.length);


                Message msg = new Message(buf, val);
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
				ObjectOutputStream oo = new ObjectOutputStream(bStream); 
				oo.writeObject(msg);
				oo.close();

				byte[] data = bStream.toByteArray();

				DatagramPacket packet_send = new DatagramPacket(data, data.length, address, port);
				socket.send(packet_send);
				val++;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
}


	public static void main(String args[]){
		try{
			str = args[0];
	       	//address=InetAddress.getByName(args[0]);
			App client = new App();
			Server app_server = new Server();
			client.start();
			app_server.start();
		}
		catch(Exception e){
			e.printStackTrace();
		}


	}

}