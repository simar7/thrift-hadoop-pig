import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.DataBag;
import org.apache.hadoop.io.WritableComparable;

public class Maximum extends EvalFunc<String> {

	public String splitter = ",";
	
	public String exec(Tuple input) throws IOException {

		try{
			String inputTuple_str = new String(input.toDelimitedString(this.splitter));
            List<String> inputList = Arrays.asList(inputTuple_str.split(","));
	        ArrayList<Integer> samples = new ArrayList<Integer>();

	        int cnt = 1;
	        Double max = 0.0;
	        for (int i = 0; i < input.size(); i++) {
	            Double t = Double.parseDouble(inputList.get(i));
	              if(t >= max) {
	              	if(t > max) {
	              		samples.clear();
	              		max = t;
	              	}
	              	samples.add(i+1);
	              }
        	}
			String str = "";
        	
        	for(int i = 0; i < samples.size(); i++) {
        		str += "gene_" + samples.get(i).toString() + ",";

        	}
        	return str.substring(0,str.length()-1);


		} catch(Exception e) {
			throw new IOException("Caught exception processing input " + e.getMessage(), e);
		}
	}
}
