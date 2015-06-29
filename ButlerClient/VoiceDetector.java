import java.io.*;
import javax.sound.sampled.*;

public class VoiceDetector{
    public static void main(String[] args) {
		TargetDataLine targetDataLine = null;
		int captureCount;
		boolean capture = false;
		byte tempBuffer[] = new byte[8000];
		int countCalm;
		short convert[] = new short[tempBuffer.length];

		String rootpath = "/home/pi/butlerclient/";

		try{
			ProcessBuilder playinitsound = new ProcessBuilder();

			playinitsound.command("aplay", rootpath + "Startup.wav");
			playinitsound.start();
		}
		catch(Exception e){}

		try {
			AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

			while(true){
				capture = !new File(rootpath + "Processing/client.listen").exists();
				captureCount = 0;
				while (capture) {
					if(targetDataLine == null){
						targetDataLine = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
					}

					targetDataLine.open(audioFormat);
					targetDataLine.start();
					targetDataLine.read(tempBuffer, 0, tempBuffer.length);

					try {
						countCalm = 0;
						for (int i = 0; i < tempBuffer.length; i++) {
							convert[i] = tempBuffer[i];
							if (convert[i] == 0) {
								countCalm++;
							}
						}
						
						capture = !(countCalm > 300);
						if(capture){
							capture = false;
							captureCount++;
						}
					}
					catch (StringIndexOutOfBoundsException e) {
						System.out.println(e.getMessage());
					}
					
					Thread.sleep(0);
					targetDataLine.stop();
				}

				if(captureCount > 0){
					ProcessBuilder playsound = new ProcessBuilder();
					playsound.command("aplay", rootpath + "Acknowledge.wav");
					playsound.start();

					File dir = new File(rootpath + "Processing"); dir.mkdirs();

					File listenCommandFile = new File(dir, "client.listen"); listenCommandFile.createNewFile();
					targetDataLine.close();
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}
