import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.io.FilenameFilter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

@SuppressWarnings("restriction")
public class Babbler{
    private static final String rootpath = "/home/pi/butlerclient/";
    private static final String processingpath = rootpath + "Processing/";

    public static void main(String[] args) throws Exception {
		while(true){
			File dir = new File(processingpath);

			File answerfile = null;
			File speakfile = null;

			//      Text-To-Speech
			File[] files = dir.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.toLowerCase().endsWith(".answer");
				}
			});

			Arrays.sort(files);

			for (File file : files) {
				answerfile = new File(file.getCanonicalPath());
				break;
			}

			if(answerfile != null){
				ProcessBuilder tts = new ProcessBuilder();
				String commandpath = processingpath + "TextToSpeech.sh";
				String processfile = processingpath + answerfile.getName().replaceAll(".answer", "");

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