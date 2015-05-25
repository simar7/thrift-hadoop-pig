import ece454750s15a1.*;

import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class A1ManagementHandler implements A1Management.Iface {

    private HashMap<String, PerfCounters> perfMap;
    private List<String> groupMembers;

    public A1ManagementHandler() {
        perfMap = new HashMap<String, PerfCounters>();
        groupMembers = Arrays.asList("s244sing", "cpinn");
    }

    boolean checkPassword(String password, String hash) {
        // TODO: Implement jBcrypt logic.
    }

    // Return performance metrics.
    PerfCounters getPerfCounters() {
        return perfMap;
    }

    // Return group member list.
    List<String> getGroupMembers() {
        return groupMembers;
    }

    // Other Interfaces

    // Join Cluster Interface

    // Periodic Learning Interface

}