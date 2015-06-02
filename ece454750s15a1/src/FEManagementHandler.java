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

    public  static CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServer.BEServerEntity>();
    public  static CopyOnWriteArrayList<FEServer.SeedEntity> seedList = new CopyOnWriteArrayList<FEServer.SeedEntity>();

    public FEManagementHandler(CopyOnWriteArrayList<FEServer.SeedEntity> seedList, CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList) {
        this.BEServerList = BEServerList;
        this.seedList = seedList;
    }

    public CopyOnWriteArrayList<BEServer.BEServerEntity> getBEServerList() {
        return this.BEServerList;
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
        BEServer.BEServerEntity beServerEntity = new BEServer.BEServerEntity();
        beServerEntity.setEntityFields(nodeName, host, pport, mport, ncores);
        BEServerList.add(beServerEntity);

        // clusterList.get(k).__debug_showInfo();

        System.out.println("[FEManagement] The [" + nodeName + "]" + " was added to the cluster");
        return true;
    }
}