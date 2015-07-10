import java.io.*;
import java.util.*;

public class SpeechToText {
	public static void main(String[] args) throws IOException{
		//	Hashmap für das Erkennen und Entfernen von .ret Dateileichen
		Map<String, Integer> returncount = new HashMap<String, Integer>();

		while(true){
			String rootpath = "/home/pi/butlerclient/Processing";

			//	Suchen nach einer Sprachdatei (.speech)
			File dir = new File(rootpath);
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".speech");
				}
			});
			File speechfile = null;
			if(files != null){
				for (File file : files) {
					speechfile = new File(file.getCanonicalPath());
				}
			}

			//	Wenn für die Sprachdatei eine Bearbeitungsdatei der GoogleSpeechApi (.ret) gefunden wurden, wird der Zähler für diese Übersetzung erhöht.
			String returnFile = speechfile == null ? "" : speechfile.getCanonicalPath().replaceAll(".speech", ".ret");
			int rc = returncount.containsKey(returnFile) ? returncount.get(returnFile) : 0;
			rc++;
			returncount.put(returnFile, rc);

			//	Wenn die 60. Erkennung der Übersetzungsdatei gefunden wurde ...
			if(rc > 60){
				//	... wird diese als GoogleSpeechApi Leiche aus dem Dateisystem und der HashMap entfernt.
				File retfile = new File(returnFile);
				if(retfile.exists()){
					retfile.delete();
					returncount.remove(returnFile);
				}
			}

			//	Wenn eine Sprachdatei gefunden wurde, die noch keine Verarbeitungsdatei der GoogleSpeechApi hat ...
			if(speechfile != null && speechfile.exists() && !new File(returnFile).exists()){
				//	wird für die Übersetzung über die GoogleApi das Bashscript "SpeechToText.sh" gestartet.
				ProcessBuilder process = new ProcessBuilder(
					String.format("%1/SpeechToText.sh", rootpath),
					speechfile.getName().replaceAll(".speech", "")
				);
				try{
				  Process runningProcess = process.start();
				  runningProcess.waitFor();
				}
				catch(Exception ex){}
			}
			
			try{
				Thread.sleep(500);
			}
			catch(Exception ex){
				ex.printStackTrace();//Prints an error if something goes wrong.
			}
		}
	}
}


