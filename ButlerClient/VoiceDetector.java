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

		String rootpath = "/home/pi/butlerclient";
		ProcessBuilder playsound = null;
		
		//	Start des 1. Anwendungsteil >> Signal für "bereit" abspielen.
		try{
			playsound = new ProcessBuilder();
			playsound.command("aplay", String.format("%1/Startup.wav", rootpath));
			playsound.start();
		}
		catch(Exception e){}

		try {
			AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, false);
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);

			while(true){
				//	Solange die Datei "client.listen" nicht existiert, wird nach einer Stimme gelauscht
				capture = !new File(String.format("%1/Processing/client.listen", rootpath)).exists();
				captureCount = 0;
				while (capture) {
					//	Verbindung zur Audioverwaltung herstellen, wenn keine besteht
					targetDataLine = targetDataLine == null ? (TargetDataLine) AudioSystem.getLine(dataLineInfo) : targetDataLine;

					//	Aufnahme von 8000 Bytes
					targetDataLine.open(audioFormat);
					targetDataLine.start();
					targetDataLine.read(tempBuffer, 0, tempBuffer.length);

					//	Versuche alle aufgenommenen Bytes zu durchlaufen
					try {
						countCalm = 0;
						//	Durchlaufen aller Bytes
						for (int i = 0; i < tempBuffer.length; i++) {
							convert[i] = tempBuffer[i];
							//	Stilles Byte (kein Ton) >> zählen
							if (convert[i] == 0) {
								countCalm++;
							}
						}
						
						//	Wenn die gezählten, stillen Bytes eine bestimmte Grenze unterschreiten, wird das Lauschen nach einer Stimme unterbrochen.
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
					
					//	Aufnahme stoppen
					targetDataLine.stop();
				}

				//	Es wurde eine Stimme erfasst
				if(captureCount > 0){
					//	Spiele Signal für Benutzer ab, damit dieser weiß, dass seine Stimme erkannt wurde und die Aufnahme der Frage beginnt.
					playsound = new ProcessBuilder();
					playsound.command("aplay", String.format("%1/Acknowledge.wav", rootpath));
					playsound.start();

					//	Erstelle das Verarbeitungsverzeichnis, falls es noch nicht existiert.
					File dir = new File(String.format("%1/Processing", rootpath)); 
					dir.mkdirs();

					//	Erstelle Signaldatei für den Programmteil "Listener"
					File listenCommandFile = new File(dir, "client.listen"); 
					listenCommandFile.createNewFile();
					
					//	Schließen der Verbindung zu der Audioverwaltung, damit der Programmteil "Listener" aufnehmen kann.
					targetDataLine.close();
				}
			}
		}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}
