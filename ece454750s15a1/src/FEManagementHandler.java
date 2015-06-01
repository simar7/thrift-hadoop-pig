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
    private CopyOnWriteArrayList<ClusterEntity> clusterList = new CopyOnWriteArrayList<ClusterEntity>();

    public class ClusterEntity {
        public String  nodeName;
        public Integer numCores;
        public String host;
        public Integer passwordPort;
        public Integer managementPort;

        public ClusterEntity() {
            this.nodeName = "UNSET";
            this.numCores = null;
            this.host = "UNSET";
            this.passwordPort = null;
            this.managementPort = null;
        }

        public void setEntityFields(String nodeName, String host, int pport, int mport, int numCores) {
            this.nodeName = nodeName;
            this.numCores = numCores;
            this.passwordPort = pport;
            this.managementPort = mport;
            this.host = host;
        }

        public void __debug_showInfo() {
            System.out.println("nodeName = " + this.nodeName);
            System.out.println("host = " + this.host);
            System.out.println("pport = " + this.passwordPort);
            System.out.println("mport = " + this.managementPort);
            System.out.println("numCores = " + this.numCores);
        }
    }

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
    public boolean joinCluster (String nodeName, String host, int pport, int mport, int ncores) throws TException {
        // Add incoming stuff to the ConcurrentList.
        ClusterEntity clusterEntity = new ClusterEntity();
        clusterEntity.setEntityFields(nodeName, host, pport, mport, ncores);
        clusterList.add(clusterEntity);

        // clusterList.get(k).__debug_showInfo();

        System.out.println("[FESeed] The [" + nodeName + "]" + " was added to the cluster");
        return true;
    }

    public void getBEServer() {
        // Add logic to retrieve an appropiate clusterEntity from the concurrentArrayList.
        // Add logic to return a BE that is most suitable for the job (look at cores)
    }

}