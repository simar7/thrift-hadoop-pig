#!/bin/sh
ant clean
ant tuxpart2
hadoop jar part2.jar Part2 inputFolder outputFolder

