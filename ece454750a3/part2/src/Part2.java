// ECE 454 A3: Part 2
// Author: Simarpreet Singh (s244sing@uwaterloo.ca)
/*
 Objective: For each gene x, report the cancer
            score Sx, at threshold 0.5

 Input:     sample_1,0.8,0.8,0.1
            sample_2,0.3,0.2,0.4

 Output:    gene_1,0.5
            gene_2,0.5
            gene_3,0.0
 */

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Part2 {

    public static class Part2Mapper extends Mapper<Object, Text, Text, DoubleWritable> {

        private Text geneID = new Text();
        private DoubleWritable geneExpValue = new DoubleWritable();
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            int geneNumber = 0;
            while (itr.hasMoreTokens()) {
                if (geneNumber == 0) {
                    itr.nextToken();    // skip sample number
                    geneNumber++;
                    continue;
                }

                geneExpValue.set(Double.parseDouble(itr.nextToken()));
                geneID.set("gene_" + geneNumber);
                geneNumber++;
                context.write(geneID, geneExpValue);
            }
        }
    }

    public static class Part2Reducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

        private DoubleWritable result = new DoubleWritable();

        public void reduce(Text geneIDKey, Iterable<DoubleWritable> geneExpValues, Context context)
                throws IOException, InterruptedException {

            // Count the number of samples
            double totalSamples = 0;
            double cancerEffectedSamples = 0;
            for (DoubleWritable geneExpVal : geneExpValues) {
                if (geneExpVal.get() > 0.5) {
                    cancerEffectedSamples++;
                }
                totalSamples++;
            }

            double cancerScore = cancerEffectedSamples / totalSamples;
            result.set(cancerScore);
            context.write(geneIDKey, result);
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: part2 <in> <out>");
            System.exit(2);
        }
        Job job = new Job(conf, "Highest Expression Value Calculator");
        job.setJarByClass(Part2.class);
        job.setMapperClass(Part2Mapper.class);
        job.setReducerClass(Part2Reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}