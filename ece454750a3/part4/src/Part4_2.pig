register Score.jar;
geneData = LOAD '$input' USING PigStorage(',');
geneSample = FOREACH geneData GENERATE (bag{tuple()})TOBAG($1..)AS genes:bag{t:tuple()};
flatSample = FOREACH geneSample GENERATE FLATTEN(genes) AS b;
sampleT = GROUP flatSample BY $0;
geneMax = FOREACH sampleT GENERATE $0, Score(flatSample);
STORE geneMax INTO '$output' USING PigStorage(',');
