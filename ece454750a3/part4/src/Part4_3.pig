register SimiCalc.jar;

samples1 = LOAD '$input' USING PigStorage(',');
samples2 = LOAD '$input' USING PigStorage(',');
crossSamples = CROSS samples1, samples2;
simiScores = FOREACH crossSamples GENERATE SimiCalc($0..);
STORE simiScores INTO '$output' USING PigStorage(',');
