#!/bin/sh
ant clean
ant tuxpart3
hadoop jar part3.jar Part3 inputFolder outputFolder

