register SimiCalc.jar;
define SimiCalculator SimiCalc(',');

samples1 = LOAD '$input' USING PigStorage(',');
samples2 = LOAD '$input' USING PigStorage(',');

/*
samples1genebag = FOREACH samples1 GENERATE $0, (bag{tuple()}) TOBAG($1 ..) AS gene1bag:bag{t:tuple()};
samples2genebag = FOREACH samples2 GENERATE $0, (bag{tuple()}) TOBAG($1 ..) AS gene2bag:bag{t:tuple()};
*/

crossSamples = CROSS samples1, samples2;
simiScores = FOREACH crossSamples GENERATE SimiCalculator($0..);
STORE simiScores INTO '$output' USING PigStorage(',');
