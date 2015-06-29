#!/bin/sh
#
LANGUAGE="de-de"
#
echo "1 Submit to Google Voice Recognition $1.speech"
#
ROOTPATH="/home/pi/butlerclient/Processing"
#
wget -q -U "Mozilla/5.0" --post-file ${ROOTPATH}/$1.speech --header="Content-Type: audio/x-flac; charset=utf-8; rate=8000" -O - "https://www.google.com/speech-api/full-duplex/v1/up?lang=${LANGUAGE}&lm=dictation&client=chromium&pair=08154711&key=AIzaSyBOti4mM-6x9WDnZIjIeyEU21OpBXqWBgw" > ${ROOTPATH}/$1.ret
echo "2 SED Extract recognized text"
#
cat ${ROOTPATH}/$1.ret | sed 's/.*utterance":"//' | sed 's/","confidence.*//' | sed 's/.*transcript":"//' | sed '1d' > ${ROOTPATH}/$1.question

if [ ! -s ${ROOTPATH}/$1.question ] ; then
  rm ${ROOTPATH}/$1.question
fi

echo "3 Remove Temporary Files"
#
rm ${ROOTPATH}/$1.speech
rm ${ROOTPATH}/$1.ret
