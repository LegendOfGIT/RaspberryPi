import java.io.*;
import java.util.*;

public class Babbler{
    private static final String rootpath = "/home/pi/butlerclient";
    private static final String processingpath = String.format("%1/Processing", rootpath);

    public static void main(String[] args) throws Exception {
		while(true){
			File dir = new File(processingpath);
			File answerfile = null;
			File speakfile = null;

			// Suche alle Antwortdateien
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".answer");
				}
			});
			//	Die Suchergebnisse müssen sortiert werden, damit der zuerst angekommene Antworttext zuerst ausgesprochen wird.
			Arrays.sort(files);

			//	Heraussuchen des zuerst gesendeten Antworttextes
			for (File file : files) {
				answerfile = new File(file.getCanonicalPath());
				break;
			}

			//	Wurde eine Antworttextdatei gefunden ...
			if(answerfile != null){
				//	... wird die Datei an das Bashscript "TextToSpeech.sh" weitergereicht, über die GoogleSpeechApi übersetzt und abgespielt.
				ProcessBuilder tts = new ProcessBuilder();
				String commandpath = String.format("%1/TextToSpeech.sh", processingpath);
				String processfile = String.format(
					"%1/%2", 
					processingpath, 
					answerfile.getName().replaceAll(".answer", "")
				);

				try{
					tts.command(commandpath, processfile);
					Process p = tts.start();
					p.waitFor();
				}
				catch(Exception e){}
			}

			Thread.sleep(300);
		}
    }
}