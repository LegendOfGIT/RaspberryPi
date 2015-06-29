import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import javaFlacEncoder.FLACFileWriter;
import com.darkprograms.speech.microphone.Microphone;
import com.darkprograms.speech.recognizer.Recognizer;
import com.darkprograms.speech.recognizer.GoogleResponse;
import com.darkprograms.speech.recognizer.GSpeechDuplex;
import com.darkprograms.speech.recognizer.GSpeechResponseListener;

 /**
   * Jarvis Speech API Tutorial
   * @author Aaron Gokaslan (Skylion)
   */
public class Listener {
	public static void main(String[] args){
		String rootpath = "/home/pi/butlerclient/";

		Microphone mic = null;
		// it record FLAC file.
		File recordfile = null;
		File speechfile = null;
		
		//You can also create your own buffer using the getTargetDataLine() method.
		while(true){
			String filename = rootpath + "Processing/" + UUID.randomUUID();
			recordfile = new File(filename + ".record");
			speechfile = new File(filename + ".speech");

			File listenerfile = new File(rootpath + "Processing/client.listen");
			if(listenerfile.exists()){
				if(mic == null){
					mic = new Microphone(FLACFileWriter.FLAC);
				}

				try{
					mic.captureAudioToFile(recordfile);//Begins recording
					Thread.sleep(4000);
					mic.close();//Stops recording

					ProcessBuilder playStopRecordingSound = new ProcessBuilder();
					playStopRecordingSound.command("aplay", rootpath + "StopRecording.wav");
					playStopRecordingSound.start();
				}
				catch(Exception ex){
					ex.printStackTrace();//Prints an error if something goes wrong.
				}

				listenerfile.delete();
			}
			else{
				try{
					Thread.sleep(200);
				}
				catch(Exception ex){
					ex.printStackTrace();//Prints an error if something goes wrong.
				}
			}

			File dir = new File("/home/pi/butlerclient/Processing");

			File[] recordfiles = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".record");
				}
			});

			for (File recfile : recordfiles) {
				speechfile = new File(recfile.toPath().toString().replaceAll(".record", ".speech"));
				recfile.renameTo(speechfile);
			}
		}
	}
}
