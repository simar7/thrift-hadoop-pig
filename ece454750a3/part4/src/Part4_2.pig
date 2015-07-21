register Score.jar;
register Grouping.jar;
geneData = LOAD '$input' USING PigStorage(',');
geneSample = FOREACH geneData GENERATE FlATTEN(Grouping($1..));
g = GROUP geneSample BY $0;
DUMP g;
geneMax = FOREACH geneSample GENERATE Score($1..);
STORE geneMax INTO '$output' USING PigStorage(',');
