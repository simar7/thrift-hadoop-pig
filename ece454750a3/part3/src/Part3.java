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
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.MapWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.lib.ChainMapper;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Part3 {

    public static class DoubleArrayListWritable implements Writable {
        //private double[] data;
        private ArrayList<Double> data = new ArrayList<Double>();

        public DoubleArrayListWritable() {

        }

        public DoubleArrayListWritable(ArrayList<Double> data) {
            this.data = data;
        }

        public ArrayList<Double> getData() {
            return this.data;
        }

        public void setData(ArrayList<Double> data) {
            this.data = data;
        }

        public void write(DataOutput out) throws IOException {
            int length = 0;
            if (this.data != null) {
                length = this.data.size();
            }

            out.writeInt(length);

            for (int i = 0; i < length; i++) {
                out.writeDouble(this.data.get(i));
            }
        }

        public void readFields(DataInput in) throws IOException {
            int length = in.readInt();
            data = new ArrayList<Double>(length);
            for (int i = 0; i < length; i++) {
                data.add(in.readDouble());
            }
        }

        public void addData(double data) {
            this.data.add(data);
        }

        public String toString() {
            if (this.data.size() == 0) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (double d : data) {
                sb.append(d).append(" ");
            }
            // trim trailing space
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
    }

    // This mapper emits a single sample at a time for Part3Mapper2 to pickup.
    public static class Part3Mapper1 extends Mapper<Object, Text, Text, DoubleArrayListWritable> {
    }


    public static class Part3Reducer extends Reducer<Text, DoubleWritable, Text, DoubleWritable> {

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

    public static int getNumberOfLinesinFile(String fileName) throws Exception{
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int totalLines = 0;
            while (br.readLine() != null) totalLines++;
            return totalLines;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // should never happen
        return 0;
    }

    public static void makeCombinationsFile(String[] otherArgs) throws Exception {

        String inputFileString = otherArgs[0];
        File inputFileDescriptor = new File(inputFileString);
        File comboFileDescriptor = new File(inputFileDescriptor.getParentFile(), "comboFile.txt");

        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFileString));

            FileWriter fileWriter = new FileWriter(comboFileDescriptor.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fileWriter);
            String line;
            String subseqLine;

            int totalLines = getNumberOfLinesinFile(inputFileString);

            line = br.readLine();

            for (int currentlineNumber = 0; currentlineNumber < totalLines; ++currentlineNumber) {
                BufferedReader br_subseqLine = new BufferedReader(new FileReader(inputFileString));
                subseqLine = line;

                // skip all previous lines before current
                for (int i = 0; i < currentlineNumber; ++i)
                    subseqLine = br_subseqLine.readLine();

                // write all subsequent lines
                int subsequentLines = totalLines - currentlineNumber - 1;
                for (int j = 0; j < subsequentLines; ++j) {
                    bw.write(line + "," + subseqLine);
                    subseqLine = br_subseqLine.readLine();
                }

                line = br.readLine();
                br_subseqLine.close();
            }
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        conf.set("mapreduce.output.textoutputformat.separator", ",");
        JobConf mainConf = new JobConf(Part3.class);

        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();

        if (otherArgs.length != 2) {
            System.err.println("Usage: part3 <in> <out>");
            System.exit(2);
        }
        makeCombinationsFile(otherArgs);

        /*
        Job job = new Job(conf, "Highest Expression Value Calculator");
        job.setJarByClass(Part3.class);
        //job.setMapperClass(Part3Mapper.class);
        job.setReducerClass(Part3Reducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));


        JobConf part3mapper1conf = new JobConf(false);
        ChainMapper.addMapper(mainConf, (Class<? extends org.apache.hadoop.mapred.Mapper<Object, Text, Text, DoubleArrayListWritable>>) Part3Mapper1.class, Object.class, Text.class, Text.class, DoubleArrayListWritable.class, true, part3mapper1conf);

        JobConf part3mapper2conf = new JobConf(false);
        ChainMapper.addMapper(mainConf, (Class<? extends org.apache.hadoop.mapred.Mapper<Object, Text, Text, DoubleArrayListWritable>>) Part3Mapper2.class, Text.class, DoubleArrayListWritable.class, );

        System.exit(job.waitForCompletion(true) ? 0 : 1);
        */
    }

}