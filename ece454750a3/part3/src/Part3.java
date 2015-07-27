// ECE 454 A3: Part 3
// Author: Simarpreet Singh (s244sing@uwaterloo.ca)
/*
 Objective: Compute the similarity between samples.

 Input:     sample_1,0.8,0.8,0.1
            sample_2,0.3,0.2,0.4

 Output:    sample_1,sample_2,0.44
*/

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.examples.WordCount;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Part3 {

    public static class Part3Mapper1 extends Mapper<Object, Text, IntWritable, Text> {
        private String sampleID;
        private Double geneExpValue;
        private Text curSample_and_gene = new Text();
        private IntWritable geneNumber = new IntWritable();
        
        public void map(Object key, Text value, Context context)
                throws IOException, InterruptedException {

            StringTokenizer itr = new StringTokenizer(value.toString(), ",");
            sampleID = itr.nextToken();
            int currentGene = 1;
 
            while (itr.hasMoreTokens()) {
                geneNumber.set(currentGene);
                geneExpValue = Double.parseDouble(itr.nextToken());
                if (geneExpValue > 0.0) {
                    curSample_and_gene.set(sampleID + "," + String.valueOf(geneExpValue));
                    context.write(geneNumber, curSample_and_gene);
                }
                currentGene++;
            }
        } 
    }


    public static void main(String[] args) throws Exception {
        Configuration conf1 = new Configuration();
        conf1.set("mapreduce.output.textoutputformat.separator", ",");
       
        String[] otherArgs = new GenericOptionsParser(conf1, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: part3 <in> <out>");
            System.exit(2);
        }
 
        Job job1 = new Job(conf1, "Part3_1");
        job1.setJarByClass(Part3.class);
        job1.setMapperClass(Part3Mapper1.class);
        //job1.setReducerClass(Part3Reducer1.class);
        job1.setMapOutputKeyClass(IntWritable.class);
        job1.setMapOutputValueClass(Text.class);
        job1.setOutputKeyClass(Text.class);
        job1.setOutputValueClass(Text.class);
        FileInputFormat.addInputPath(job1, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job1, new Path(otherArgs[1]));
        //job1.waitForCompletion(true);
        System.exit(job1.waitForCompletion(true) ? 0 : 1);
    }
}
