import java.io.*;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.net.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class QuestionTeller{
    public static void main(String[] args) throws Exception {
		// The server to connect to and our details.
		String server = "irc.freenode.net";
		String nick = "QuestionTeller";
		String oraclenick = "MisterOracle";

        // The channel which the bot will join.
        String channel = "#butlerclient";

        // Connect directly to the IRC server.
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

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream( ), "iso-8859-1"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream( ), "iso-8859-1"));

        // Log on to the server.
        writer.write("NICK " + nick + "\r\n");
        writer.write("USER " + nick + " 8 * : Java IRC Hacks Bot\r\n");
        writer.flush( );

        // Read lines from the server until it tells us we have connected.
        String line = null;
        while ((line = reader.readLine( )) != null) {
            if (line.indexOf("004") >= 0) {
                // We are now logged in.
                break;
            }
            else if (line.indexOf("433") >= 0) {
                System.out.println("Nickname is already in use.");
                return;
            }
        }

        // Join the channel.
        writer.write("JOIN " + channel + "\r\n");
        writer.flush( );

        String rootpath = "/home/pi/butlerclient/";
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMddHHmmss");

        // Keep reading lines from the server.
        while ((line = reader.readLine( )) != null) {
			String message = line.toLowerCase();
            if (message.startsWith("ping ")) {
                // We must respond to PINGs to avoid being disconnected.
                writer.write("PONG " + line.substring(5) + "\r\n");
                writer.write("PRIVMSG " + channel + " :I got pinged!\r\n");
                writer.flush( );
            }
            else {
                // Print the raw line received by the bot.
                System.out.println(message);

				// Wurde der Bot persönlich angeschrieben?
				if(message.contains("privmsg " + nick.toLowerCase())){
					// Regelmäßige Anfrage nach einer vorliegenden Frage
					if(message.contains("question?")){
						String question = "";
						File dir = new File(rootpath + "Processing");
						File[] files = dir.listFiles(new FilenameFilter() {
							public boolean accept(File dir, String name) {
								return name.toLowerCase().endsWith(".question");
							}
						});

						for (File file : files) {
							byte[] encoded = Files.readAllBytes(new File(file.getCanonicalPath()).toPath());
							question = new String(encoded, "iso-8859-1");
							file.delete();
							break;
						}

						if(question != ""){
							writer.write("PRIVMSG " + oraclenick + " :" + question + "\r\n");
							writer.flush();

							ProcessBuilder playTransmittedSound = new ProcessBuilder();
							playTransmittedSound.command("aplay", rootpath + "Sounds/transmitted.wav");

							playTransmittedSound.start();
						}
					}	
					// Empfangen einer Antwort
					else if(message.contains("answer:")){
						String[] tokens = line.split(":");
						String answer = tokens != null && tokens.length > 0 ? tokens[tokens.length - 1] : "";

						if(!answer.startsWith("Answer")){
							Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(rootpath + "Processing/" + dateformat.format(new Date()) + ".answer"), "utf-8"));
							out.write(answer);
							out.close();
						}
					}
				}
            }
        }
    }
}
