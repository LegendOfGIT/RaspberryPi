#!/bin/sh
#
ROOTPATH="/home/pi/butlerclient/Processing"
#
echo "1 Load Text for TTS Translation"
answerfile=$ROOTPATH/$1
text=`cat $1.answer`
echo $text

echo "2 Request Speech from Google TTS"
wget -q -U Mozilla -O ${ROOTPATH}/output.mp3 "http://translate.google.com/translate_tts?ie=UTF-8&tl=de&q=$text"

echo "3 Babble"
mpg321 ${ROOTPATH}/output.mp3

echo "4 Remove Temporary Files"
rm ${ROOTPATH}/output.mp3
rm $1.answer
