import ece454750s15a1.*;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


public class BEManagementHandler implements BEManagement.Iface {

    private PerfCounters perfList;
    private List<String> groupMembers;

    public BEManagementHandler() {
        perfList = new PerfCounters();
        groupMembers = Arrays.asList("s244sing", "cpinn");
    }

    // Return performance metrics.
    public PerfCounters getPerfCounters() {
        return perfList;
    }

    // Return group member list.
    public List<String> getGroupMembers() {
        return groupMembers;
    }

    // Other Interfaces

    // Join Cluster Interface
    public boolean joinCluster (String host, int pport, int mport, int ncores) {
        // TODO: check if anything needs to be done?
        return true;
    }

    // Periodic Learning Interface

}