#!/bin/sh
rm -r outputFolder
rm *.log
rm *.jar *.class
CLASSPATH=$(hadoop classpath):$(find /usr/lib/pig/pig-0.14.0/pig-0.14.0-core-h2.jar -name "*.jar"):$(find /usr/hdp/current/pig-client/ -name "*.jar")
javac -cp $CLASSPATH Maximum.java
jar -cf Maximum.jar Maximum*.class
pig -param input="../inputFolder/sampledata.txt" -param output="outputFolder" Part4_1.pig
