
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.pig.EvalFunc;
import org.apache.pig.data.Tuple;

public class SimiCalc extends EvalFunc<String> {

    public String splitter = ",";

    public SimiCalc(String splitter) {
        this.splitter = splitter;
    }

    public String exec(Tuple input) throws IOException {
        try {
            String inputTuple_str = new String(input.toDelimitedString(this.splitter));
            List<String> inputList = Arrays.asList(inputTuple_str.split(","));
            //System.out.println("----------inputList = " + inputList.toString());

            String sampleA_string = inputList.get(0);
            String sampleB_string = "0xDEADBEEF";
            ArrayList<Double> sampleA_XPValues = new ArrayList<Double>();
            ArrayList<Double> sampleB_XPValues = new ArrayList<Double>();
            ArrayList<Double> similarityList   = new ArrayList<Double>();
            double resultSimiScore = 0.0;
            boolean secondHalf = false;

            for (int i = 1; i < input.size(); i++) {
                if (secondHalf == false) {
                    // LOL HAX
                    if (inputList.get(i).matches("sample_\\d*")) {
                        secondHalf = true;
                        sampleB_string = inputList.get(i);
                    } else {
                        //System.out.println("inputList.get(" + i + ") = "+inputList.get(i));
                        sampleA_XPValues.add(Double.parseDouble(inputList.get(i)));
                    }
                } else {
                    sampleB_XPValues.add(Double.parseDouble(inputList.get(i)));
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
