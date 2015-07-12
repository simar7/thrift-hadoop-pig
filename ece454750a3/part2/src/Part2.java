// ECE 454 A3: Part 2
// Author: Simarpreet Singh (s244sing@uwaterloo.ca)
/*
 Objective: Find the highest expression value
            in the given input files.
 Input:     sample_1,0.8,0.8,0.1
            sample_2,0.3,0.2,0.4

 Output:    gene_1,0.5
            gene_2,0.5
            gene_3,0.0
 */

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

public class Part2 {

    public static class StringArrayListWritable implements Writable {
        private ArrayList<String> data = new ArrayList<String>();

        public StringArrayListWritable() {

        }

        public StringArrayListWritable(ArrayList<String> data) {
            this.data = data;
        }

        public void addData(int geneNumber) {
            this.data.add("gene_" + geneNumber);
        }

        public void recycleData(int geneNumber) {
            this.data.clear();
            this.addData(geneNumber);
        }

        public void write(DataOutput out) throws IOException {
            int length = 0;
            if (this.data != null) {
                length = this.data.size();
            }

            out.writeInt(length);

            for (int i = 0; i < length; i++) {
                out.writeUTF(this.data.get(i));
            }
        }

        public void readFields(DataInput in) throws IOException {
            int length = in.readInt();
            data = new ArrayList<String>();
            for (int i = 0; i < length; i++) {
                data.add(in.readUTF());
            }
        }

        public String toString() {
            if (this.data.size() == 0) {
                return "";
            }

            StringBuilder sb = new StringBuilder();
            for (String s : data) {
                sb.append(s).append(",");
            }
            // trim trailing space
            sb.setLength(sb.length() - 1);
            return sb.toString();
        }
    }

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

    public static class Part2Mapper extends Mapper<Object, Text, Text, StringArrayListWritable> {

        private Text sampleID = new Text();
        //private DoubleWritable highestExpValue = new DoubleWritable();
        //private DoubleArrayListWritable listOfGenes = new DoubleArrayListWritable();
        private StringArrayListWritable stringListOfGenes = new StringArrayListWritable();

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
                    //listOfGenes.addData(curExpValue);
                    // Recycle if we already have existing values
                    if (maxExpValue != 0 && maxExpValue != curExpValue) {
                        stringListOfGenes.recycleData(geneNumber);
                    } else {
                        stringListOfGenes.addData(geneNumber);
                    }
                    maxExpValue = curExpValue;
                }
                geneNumber++;
            }
            //highestExpValue.set(maxExpValue);
            context.write(sampleID, stringListOfGenes);
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
        // Set number of reducers to 0
        job.setNumReduceTasks(0);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(DoubleWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}