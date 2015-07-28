register Maximum.jar;
geneData = LOAD '$input' USING PigStorage(',');
geneMax = FOREACH geneData GENERATE $0, Maximum($1..);
STORE geneMax INTO '$output' USING PigStorage(',');
