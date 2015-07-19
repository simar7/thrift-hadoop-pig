#!/bin/sh
rm *.log
rm *.jar *.class
CLASSPATH=$(hadoop classpath):$(find /usr/lib/pig/pig-0.14.0/pig-0.14.0-core-h2.jar -name "*.jar"):$(find /usr/hdp/current/pig-client/ -name "*.jar")
javac -cp $CLASSPATH  SimiCalc.java
jar -cf SimiCalc.jar SimiCalc*.class
