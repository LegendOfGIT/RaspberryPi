import java.io.*;
import java.util.*;
import javaFlacEncoder.FLACFileWriter;
import com.darkprograms.speech.microphone.Microphone;

public class Listener {
	public static void main(String[] args){
		String rootpath = "/home/pi/butlerclient";

		//	Microphoneklasse aus "DarkprogramsSpeech.jar" für Tonaufnahme und Konvertierung in eine FLAC-Datei
		Microphone mic = null;
		//	Aufnahme- und Sprachdatei
		File recordfile = null;
		File speechfile = null;

		while(true)
		{
			//	Vorbereitung der Aufnahme- und Sprachdatei
			String filename = String.format("%1/Processing/%2" , rootpath, UUID.randomUUID());
			recordfile = new File(filename + ".record");
			speechfile = new File(filename + ".speech");

			//	Wenn die Datei "client.listen" von dem Programmteil "VoiceDetector" erstellt wurde, wird von dem Programmteil "Listener" die Sprache/Frage aufgenommen.
			File listenerfile = new File(String.format("%1/Processing/client.listen", rootpath));
			if(listenerfile.exists()){
				//	Mikrofon für Aufnahme instanziieren, wenn noch keine Instanz vorliegt.
				mic = mic == null ? new Microphone(FLACFileWriter.FLAC) : mic;

				try{
					//	Aufnahme beginnen ...
					mic.captureAudioToFile(recordfile);
					//	4 Sekunden aufnehmen
					Thread.sleep(4000);
					//	Ausnahme stoppen
					mic.close();

					//	Benutzer mit einem Geräusch signalisieren, dass die Sprache zu Ende aufgenommen wurde
					ProcessBuilder playSound = new ProcessBuilder();
					playSound.command("aplay", String.format("%1/StopRecording.wav", rootpath));
					playSound.start();
				}
				catch(Exception ex){
					ex.printStackTrace();
				}

				//	Nach der Aufnahme wird die Datei "client.listen" wird gelöscht, damit der Programmteil "VoiceDetector" wieder nach einer Stimme sucht.
				listenerfile.delete();
			}
			//	Wurde keine Stimme erkannt, wird nach 200 Millisekunden wieder geprüft, ob eine Stimme erkannt wurde.
			else{
				try{
					Thread.sleep(200);
				}
				catch(Exception ex){
					ex.printStackTrace();//Prints an error if something goes wrong.
				}
			}

			//	Suche im Verarbeitungsverzeichnis, alle Aufnahmedateien
			File dir = new File(String.format("%1/Processing", rootpath));
			File[] recordfiles = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".record");
				}
			});

			//	Durchlaufe alle gefundenen Aufnahmedateien
			for (File recfile : recordfiles) {
				//	Benenne die abgeschlossenen Aufnahmen (.record) in Sprachdateien (.speech) um.
				//	Die Sprachdateien werden von dem Programmteil SpeechToText über die GoogleSpeechApi in Text übersetzt.
				speechfile = new File(recfile.toPath().toString().replaceAll(".record", ".speech"));
				recfile.renameTo(speechfile);
			}
		}
	}
}
