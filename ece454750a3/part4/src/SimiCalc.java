
import java.io.IOException;
import java.util.ArrayList;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class SimiCalc extends EvalFunc<String> {

    public String exec(Tuple input) throws IOException {
        try {
            String sampleA_string = (String) input.get(0);
            String sampleB_string = "0xDEADBEEF";
            ArrayList<Double> sampleA_XPValues = new ArrayList<Double>();
            ArrayList<Double> sampleB_XPValues = new ArrayList<Double>();
            ArrayList<Double> similarityList   = new ArrayList<Double>();
            double resultSimiScore = 0.0;
            boolean secondHalf = false;

            for (int i = 1; i < input.size(); i++) {
                if (secondHalf == false) {
                    if (input.get(i) instanceof String) {
                        secondHalf = true;
                        sampleB_string = (String)input.get(i);
                    } else {
                        sampleA_XPValues.add(Double.parseDouble(input.get(i).toString()));
                    }
                } else {
                    sampleB_XPValues.add(Double.parseDouble(input.get(i).toString()));
                }
            }

            for (int i = 0; i < sampleA_XPValues.size(); i++) {
                similarityList.add(sampleA_XPValues.get(i) * sampleB_XPValues.get(i));
                resultSimiScore += similarityList.get(i);
            }

            String returnSimiString = sampleA_string + "," + sampleB_string + "," + String.valueOf(resultSimiScore);
            return returnSimiString;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0xDEADPONY";
    }
}
