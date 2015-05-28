import ece454750s15a1.*;
import org.apache.thrift.TException;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class FEManagementHandler implements FEManagement.Iface {

    private PerfCounters perfList;
    private List<String> hostList;
    private List<String> groupMembers;
    private ConcurrentHashMap<String, Integer> hostCoreMap;
    private ConcurrentHashMap<Integer, Integer> portsMap;


    public FEManagementHandler() {
        perfList = new PerfCounters();
        hostList = new ArrayList<String>();
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
    public boolean joinCluster (String host, int pport, int mport, int ncores) throws TException{
        if((hostCoreMap.put(host, ncores) != null) && (portsMap.put(pport, mport) != null)) {
            return true;
        }
        return false;
    }


}