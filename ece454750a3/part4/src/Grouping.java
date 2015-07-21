   import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;
import org.apache.pig.data.DataBag;
import org.apache.pig.data.BagFactory;
import org.apache.pig.data.TupleFactory;
import org.apache.hadoop.io.WritableComparable;

public class Grouping extends EvalFunc<DataBag> {
    
    public String splitter = ",";

    public DataBag exec(Tuple input) throws IOException {

        BagFactory bf = BagFactory.getInstance();
        TupleFactory tf = TupleFactory.getInstance();

        try{
            String inputTuple_str = new String(input.toDelimitedString(this.splitter));
            List<String> inputList = Arrays.asList(inputTuple_str.split(","));

            DataBag output = bf.newDefaultBag();

            int geneNumber = 1;
            String str = "";
            for(int i = 0; i< input.size(); i++) {
                HashMap<String,String> in =new HashMap<String,String>();
                in.put("gene_" + String.valueOf(geneNumber), inputList.get(i).toString());

                output.add(tf.newTuple(in));
                geneNumber++;
            }

            return output;


        } catch(Exception e) {
            throw new IOException("Caught exception processing input " + e.getMessage(), e);
        }
    }
}

   