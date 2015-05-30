import ece454750s15a1.*;
import org.apache.thrift.TException;

import java.lang.Exception;
import java.lang.Integer;
import java.lang.String;
import java.lang.System;
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
        hostCoreMap = new ConcurrentHashMap<String, Integer>(100);
        portsMap = new ConcurrentHashMap<Integer, Integer>(100);
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
    public boolean joinCluster (String nodeName, String host, int pport, int mport, int ncores) throws TException{

        Integer hcmp_retval = hostCoreMap.put(host, ncores);
        Integer pmmp_retval = portsMap.put(pport, mport);

        // Should never get hit assuming there are unique nodes in the system.
        if( (hcmp_retval != null) || (pmmp_retval != null) ) {
            System.out.println("The [" + nodeName + "]" + " was a dupe.");

            // Debug info
            System.out.println("host, pport, mport, ncores = " + host + " " + pport + " " + mport + " " + " " + ncores);
            for (ConcurrentHashMap.Entry<String, Integer> entry : hostCoreMap.entrySet()) {
                String key = entry.getKey().toString();
                Integer value = entry.getValue();
                System.out.println("key, " + key + " value " + value);
            }

            for (ConcurrentHashMap.Entry<Integer, Integer> entry : portsMap.entrySet()) {
                String key = entry.getKey().toString();
                Integer value = entry.getValue();
                System.out.println("key, " + key + " value " + value);
            }
            return false;
        }

        System.out.println("The [" + nodeName + "]" + " was added to the cluster");
        return true;
    }


}