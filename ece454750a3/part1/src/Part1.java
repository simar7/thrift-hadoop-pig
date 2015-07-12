// ECE 454 A3: Part 1
// Author: Simarpreet Singh (s244sing@uwaterloo.ca)
/*
 Objective: Find the highest expression value
            in the given input files.
 Input:     sample_1: 0.8, 0.8, 0.5
 Output:    sample_1: 0.8, 0.8

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
import org.apache.tomcat.dbcp.pool.impl.GenericKeyedObjectPool;


public class Part1 {

    public static class Part1Mapper
            extends Mapper<Object, Text, Text, DoubleWritable> {

        private Text sampleID = new Text();
        private DoubleWritable highestExpValue = new DoubleWritable();

        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            int geneNumber = 0;
            double maxExpValue = 0;
            double curExpValue = 0;
            while (itr.hasMoreTokens()) {
                if (geneNumber == 0) {
                    sampleID.set(itr.nextToken());
                    geneNumber++;
                    continue;
                }
                if ((curExpValue = Double.parseDouble(itr.nextToken())) >= maxExpValue) {
                    maxExpValue = curExpValue;
                }
                geneNumber++;
            }
            highestExpValue.set(maxExpValue);
            context.write(sampleID, highestExpValue);
        }
    }


    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: part1 <in> <out>");
            System.exit(2);
        }
        Job job = new Job(conf, "Highest Expression Value Calculator");
        job.setJarByClass(Part1.class);
        job.setMapperClass(Part1Mapper.class);
        // Set number of reducers to 0
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}