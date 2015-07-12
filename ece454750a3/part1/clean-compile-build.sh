#!/bin/sh
ant clean
ant tuxpart1
hadoop jar part1.jar Part1 inputFolder outputFolder

