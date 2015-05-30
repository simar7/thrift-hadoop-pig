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
import java.util.concurrent.CopyOnWriteArrayList;


public class FEManagementHandler implements FEManagement.Iface {

    private PerfCounters perfList;
    private List<String> hostList;
    private List<String> groupMembers;
    //private ConcurrentHashMap<String, Integer> hostCoreMap;
    //private ConcurrentHashMap<Integer, Integer> portsMap;

    public class clusterEntity {
        public String  nodeName;
        public Integer numCores;
        public Integer passwordPort;
        public Integer managementPort;
    }

    private CopyOnWriteArrayList<clusterEntity> clusterList = new CopyOnWriteArrayList<clusterEntity>();

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

        // Add incoming stuff to the ConcurrentList.


        System.out.println("The [" + nodeName + "]" + " was added to the cluster");
        return true;

        // Old logic that relies on ConcurrentHashMaps, not really useful when running on single machine with many BEs.
        /*
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
        */
    }

    public String [] getBEServer() {
        // Add logic to retrieve an appropiate clusterEntity from the concurrentArrayList.
        // Add logic to return a BE that is most suitable for the job (look at cores)

    }


}