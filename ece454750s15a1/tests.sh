#!/bin/bash

#need to create some FESeeds, BEServes, FEServers, and clients

#echo "Hello"
/usr/bin/thrift --gen java -r ece454750s15a1.thrift

java -Xmx128M -cp "ece454750s15a1.jar:../lib/*" FEServer -host localhost -pport 19999 -mport 29999 -ncores 2 -seeds localhost:29999,localhost:49999
#java -Xmx128M -cp "ece454750s15a1.jar:../lib/*" FEServer -host localhost -pport 39999 -mport 49999 -ncores 2 -seeds localhost:29999,localhost:49999
#java -Xmx128M -cp "ece454750s15a1.jar:../lib/*" BEServer -host localhost -pport 11236 -mport 11237 -ncores 2 -seeds localhost:29999,localhost:49999