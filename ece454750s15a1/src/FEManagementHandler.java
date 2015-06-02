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
import java.util.Collections;


public class FEManagementHandler implements FEManagement.Iface {

    public static class BEServerEntity {
        public String  nodeName;
        public Integer numCores;
        public String host;
        public Integer passwordPort;
        public Integer managementPort;

        public BEServerEntity() {
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

        public String [] getBEHostNamePortNumber() {
            String [] ArrRet = {this.host, this.passwordPort.toString()};
            return ArrRet;
        }

        public String [] getBEHostNamePortNumberCores() {
            String [] ArrRet = {this.host, this.passwordPort.toString(), Integer.toString(this.numCores)};
            return ArrRet;
        }

        public String getBEHostName() {
            return this.host;
        }

        public int getBEManagementPortNumber() {
            return this.managementPort;
        }

        public int getBEPasswordPortNumber() {
            return this.passwordPort;
        }

        public void __debug_showInfo() {
            System.out.println("nodeName = " + this.nodeName);
            System.out.println("host = " + this.host);
            System.out.println("pport = " + this.passwordPort);
            System.out.println("mport = " + this.managementPort);
            System.out.println("numCores = " + this.numCores);
        }

    }

    public static class SeedEntity {
        public String seedHostName;
        public Integer seedPort;

        public SeedEntity() {
            this.seedHostName = "UNSET";
            this.seedPort = null;
        }

        public void setEntityFields(String seedHostName, int port) {
            this.seedHostName = seedHostName;
            this.seedPort = port;
        }

        public String[] getEntityFields() {
            String[] seedArrRet = {this.seedHostName, this.seedPort.toString()};
            return seedArrRet;
        }

        public String getSeedHostName() {
            return this.seedHostName;
        }

        public int getSeedPortNumber() {
            return this.seedPort;
        }

        public void __debug_showInfo() {
            System.out.println("seedHostName = " + this.seedHostName);
            System.out.println("seedPort = " + this.seedPort);
        }
    }

    public  static CopyOnWriteArrayList<BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServerEntity>();
    public  static CopyOnWriteArrayList<SeedEntity> seedList = new CopyOnWriteArrayList<SeedEntity>();

    public FEManagementHandler(CopyOnWriteArrayList<SeedEntity> seedList, CopyOnWriteArrayList<BEServerEntity> BEServerList) {
        this.BEServerList = BEServerList;
        this.seedList = seedList;
    }

    // Return performance metrics.
    public PerfCounters getPerfCounters() {
        PerfCounters perfList = new PerfCounters();
        return perfList;
    }

    // Return group member list.
    public List<String> getGroupMembers() {
        ArrayList<String> groupMembers = new ArrayList<String>();
        groupMembers.add("s244sing");
        groupMembers.add("cpinn");
        return groupMembers;
    }

    // Other Interfaces

    // Join Cluster Interface
    public boolean joinCluster (String nodeName, String host, int pport, int mport, int ncores) throws TException {
        // Add incoming stuff to the ConcurrentList.
        BEServerEntity beServerEntity = new BEServerEntity();
        beServerEntity.setEntityFields(nodeName, host, pport, mport, ncores);
        BEServerList.add(beServerEntity);

        // clusterList.get(k).__debug_showInfo();

        System.out.println("[FEManagement] The [" + nodeName + "]" + " was added to the cluster");
        return true;
    }
}