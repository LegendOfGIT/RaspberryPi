#!/bin/bash
javac VoiceDetector.java
javac -cp jmf.jar:JavaGoogleText.jar:JavaFlacEncoder.jar:. Listener.java
javac SpeechToText.java
javac -cp jmf.jar:JavaGoogleText.jar:JavaFlacEncoder.jar:. QuestionTeller.java
javac -cp jmf.jar:JavaGoogleText.jar:JavaFlacEncoder.jar:. Babbler.java
