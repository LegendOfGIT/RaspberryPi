import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;

public class SpeechToText {
	public static void main(String[] args) throws IOException{
		Map<String, Integer> returncount = new HashMap<String, Integer>();

		//You can also create your own buffer using the getTargetDataLine() method.
		while(true){
			String rootpath = "/home/pi/butlerclient/Processing";

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

			String returnFile = speechfile == null ? "" : speechfile.getCanonicalPath().replaceAll(".speech", ".ret");

			int rc = returncount.containsKey(returnFile) ? returncount.get(returnFile) : 0;
			rc++;
			returncount.put(returnFile, rc);

			if(rc > 60){
				File retfile = new File(returnFile);
				if(retfile.exists()){
					retfile.delete();
					returncount.remove(returnFile);
				}
			}

			if(speechfile != null && speechfile.exists() && !new File(returnFile).exists()){
				ProcessBuilder process = new ProcessBuilder(
					rootpath + "/SpeechToText.sh",
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


