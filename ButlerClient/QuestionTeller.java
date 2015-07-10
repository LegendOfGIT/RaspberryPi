import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

public class QuestionTeller{
    public static void main(String[] args) throws Exception {
		// IRC-Server und Channel
		String server = "irc.freenode.net";
        String channel = "#butlerclient";
		// Username des Programmteil "QuestionTeller"
		String nick = "QuestionTeller";
		// Username des Antwortsystems (MVC-Anwendung)
		String oraclenick = "MisterOracle";

        // Kann die Verbindung zum IRC-Server nicht direkt aufgenommen werden, 
		// wird mit einem Abstand von einer halben Sekunde 10 Mal versucht erneut zu verbinden.
		int tries = 10;
        Socket socket = null;
		while(tries > 0){
			try{
				socket = new Socket(server, 6667);
				tries = 0;
			}
			catch(Exception ex){
				tries--;
			}

			try{
				Thread.sleep(500);
			}
			catch(Exception ex){
			}
		}

		// Bereite Buffer für das Lesen und Schreiben mit einer Westeuropäischen Codierung vor.
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream( ), "iso-8859-1"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream( ), "iso-8859-1"));

        // Login des QuestionTellers auf dem IRC-Server
        writer.write(String.format("NICK %1\r\n", nick));
        writer.write(String.format("USER %1 8 * : Java IRC Hacks Bot\r\n", nick));
        writer.flush( );

        // 	Einlesen aller Zeilen vom IRC-Server bis ...
        String line = null;
        while ((line = reader.readLine( )) != null) {
			//	... der Code für "eingeloggt" gesendet wurde
            if (line.indexOf("004") >= 0) {
                break;
            }
			//	... der Code für "Nickname wird bereits verwendet" gesendet wurde
            else if (line.indexOf("433") >= 0) {
                System.out.println("Nickname is already in use.");
                return;
            }
        }

        // Nach dem Login, wird der festgelegt Channel betreten.
        writer.write(String.format("JOIN %1\r\n", channel));
        writer.flush( );

        String rootpath = "/home/pi/butlerclient";
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");

        //	Reagiere auf weitere, vom Server gesendete Zeilen
        while ((line = reader.readLine( )) != null) {
			String message = line.toLowerCase();
			
			//	Wenn der Server einen Ping anfordert  ...
            if (message.startsWith("ping ")) {
                // ... Antwortet der Client mit Pong. Ohne die Antwort wird der Client vom Server abgemeldet
                writer.write(String.format("PONG %1\r\n", line.substring(5)));
                writer.write(String.format("PRIVMSG %1 :I got pinged!\r\n", channel));
                writer.flush( );
            }
            else {
				// Wurde der Bot persönlich angeschrieben?
				if(message.contains("privmsg " + nick.toLowerCase())){
					// Regelmäßige Anfrage nach einer vorliegenden Frage
					if(message.contains("question?")){
						String question = "";
						
						// Suche alle Fragetext-Dateien (.question) aus dem Verarbeitungsverzeichnis
						File dir = new File(String.format("%1/Processing", rootpath));
						File[] files = dir.listFiles(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".question");
							}
						});

						// Lese die erste Fragetext-Datei mit Westeuropäischer Codierung ein und lösche anschließend die Fragetextdatei
						for (File file : files) {
							byte[] encoded = Files.readAllBytes(new File(file.getCanonicalPath()).toPath());
							question = new String(encoded, "iso-8859-1");
							file.delete();
							break;
						}

						// Wurde aus der Fragetext-Datei ein Fragetext eingelesen ...
						if(question != ""){
							//	... wird der Text über den Chat an das Antwortsystem (MVC-Anwendung) über den IRC-Chat gesendet. 
							writer.write(String.format("PRIVMSG %1 :%2\r\n", oraclenick, question));
							writer.flush();

							//	... spiele ein Signal für den Benutzer ab, damit dieser weiß, dass die Frage an das Antwortsystem gesendet wurde.
							ProcessBuilder playSound = new ProcessBuilder();
							playSound.command("aplay", String.format("%1/Sounds/transmitted.wav", rootpath));
							playSound.start();
						}
					}	
					// Empfangen einer Antwort
					else if(message.contains("answer:")){
						//	Zerlege Antwortzeile in den Antworttext
						String[] tokens = line.split(":");
						String answer = tokens != null && tokens.length > 0 ? tokens[tokens.length - 1] : "";

						//	Wurde keine Leerantwort gesendet ...
						if(!answer.startsWith("Answer")){
							//	... wird der Antworttext in einer Antwortdatei (.answer) gespeichert und anschließend vom Programmteil "Babbler" übersetzt und abgespielt
							Writer out = new BufferedWriter(
								new OutputStreamWriter(
									new FileOutputStream(String.format(
										"%1/Processing/%2.answer", 
										rootpath, 
										dateformat.format(new Date())
									)), 
									"utf-8"
								)
							);
							out.write(answer);
							out.close();
						}
					}
				}
            }
        }
    }
}
