import ece454750s15a1.*;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class A1ManagementHandler implements A1Management.Iface {

    private PerfCounters perfList;
    private List<String> groupMembers;

    public A1ManagementHandler() {
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

    // Periodic Learning Interface

}