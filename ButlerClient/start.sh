#!/bin/sh
sudo rm /home/pi/butlerclient/Processing/*.question
sudo rm /home/pi/butlerclient/Processing/*.speech
sudo rm /home/pi/butlerclient/Processing/*.ret
sudo rm /home/pi/butlerclient/Processing/client.listen

sudo java -cp /home/pi/butlerclient:. VoiceDetector &
sudo java -cp /home/pi/butlerclient:/home/pi/butlerclient/JavaFlacEncoder.jar:/home/pi/butlerclient/JavaGoogleText.jar:. Listener &
sudo java -cp /home/pi/butlerclient:/home/pi/butlerclient/JavaFlacEncoder.jar:/home/pi/butlerclient/JavaGoogleText.jar:. SpeechToText &
sudo java -cp /home/pi/butlerclient:/home/pi/butlerclient/JavaFlacEncoder.jar:/home/pi/butlerclient/JavaGoogleText.jar:. QuestionTeller &
sudo java -Dfile,encoding=UTF-8 -cp /home/pi/butlerclient:/home/pi/butlerclient/JavaFlacEncoder.jar:/home/pi/butlerclient/JavaGoogleText.jar:. Babbler
