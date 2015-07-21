import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.DataBag;
import org.apache.hadoop.io.WritableComparable;

public class Score extends EvalFunc<String> {
	
	public String splitter = ",";

	public String exec(Tuple input) throws IOException {

		try{
			String inputTuple_str = new String(input.toDelimitedString(this.splitter));
            List<String> inputList = Arrays.asList(inputTuple_str.split(","));

	       	Double totalSamples = 0.0;
            Double cancerEffectedSamples = 0.0;
	        for(int i = 1; i< input.size(); i++) {
                if (Double.parseDouble(inputList.get(i)) > 0.5) {
                    cancerEffectedSamples++;
                }
                totalSamples++;
            }

            Double cancerScore = cancerEffectedSamples / totalSamples;

        	return cancerScore.toString();


		} catch(Exception e) {
			throw new IOException("Caught exception processing input " + e.getMessage(), e);
		}
	}
}