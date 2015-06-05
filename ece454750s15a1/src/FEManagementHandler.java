import ece454750s15a1.*;

import org.apache.thrift.TException;

import java.lang.*;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.lang.String;
import java.lang.System;
import java.util.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;


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

    public static class FEServerEntity {
        public String nodeName;
        public Integer numCores;
        public String host;
        public Integer passwordPort;
        public Integer managementPort;

        public FEServerEntity() {
            this.nodeName = "UNSET";
            this.numCores = null;
            this.host = "UNSET";
            this.passwordPort = null;
            this.managementPort = null;
        }

        public FEServerEntity(String nodeName, Integer numCores, Integer passwordPort, Integer managementPort, String host) {
            this.nodeName = nodeName;
            this.numCores = numCores;
            this.passwordPort = passwordPort;
            this.managementPort = managementPort;
            this.host = host;
        }

        public void setEntityFields(String nodeName, String host, int pport, int mport, int numCores) {
            this.nodeName = nodeName;
            this.numCores = numCores;
            this.passwordPort = pport;
            this.managementPort = mport;
            this.host = host;
        }
        public String[] getEntityFields() {
            String[] FEServerArrRet = {this.nodeName, this.passwordPort.toString(), this.managementPort.toString()};
            return FEServerArrRet;
        }

        public String getFEServerHostName() {
            return this.host;
        }

        public void __debug_showInfo() {
            System.out.println("FEServerHostName = " + this.host);
            System.out.println("FEServerManagementPort = " + this.managementPort);
            System.out.println("FEServerPasswordPort = " + this.passwordPort);
        }
    }

    private CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList = null;
    private CopyOnWriteArrayList<FEServer.FEServerEntity> FEServerList = null;
    private CopyOnWriteArrayList<FEServer.SeedEntity> seedList = new CopyOnWriteArrayList<FEServer.SeedEntity>();

    private PerfCounters perfManager = null;
    private PerfCounters perfCounter = new PerfCounters();
    private Long uptime;

    public FEManagementHandler(CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList, CopyOnWriteArrayList<FEServer.FEServerEntity> FEServerList, PerfCounters perfManager, Long uptime) {
        this.BEServerList = BEServerList;
        this.FEServerList = FEServerList;
        this.uptime = uptime;
        this.perfManager = perfManager;
    }

    public PerfCounters getPerfCounters() {
        System.out.println("[BEManagementHandler] FEServer Perf counters was invoked");
        perfCounter.numSecondsUp = (int)(System.currentTimeMillis() - uptime);
        perfCounter.numRequestsReceived = perfManager.numRequestsReceived;
        perfCounter.numRequestsCompleted = perfManager.numRequestsCompleted;
        return perfCounter;
    }

    public List<String> getBEServerList() {
        try {
            List<String> beServerListToReturn = new ArrayList<String>();
            for (int i = 0; i < BEServerList.size(); i++) {
                if (!BEServerList.get(i).getBENodeName().equals("UNSET")) {
                    beServerListToReturn.add(BEServerList.get(i).getBENodeName());
                    beServerListToReturn.add(Integer.toString(BEServerList.get(i).getBECores()));
                    beServerListToReturn.add(Integer.toString(BEServerList.get(i).getBEPasswordPortNumber()));
                    beServerListToReturn.add(Integer.toString(BEServerList.get(i).getBEManagementPortNumber()));
                    beServerListToReturn.add(BEServerList.get(i).getBEHostName());
                    //BEServerList.get(i).__debug_showInfo();
                } else {
                    System.out.println("[FEManagementHandler] No more relvant data to send!");
                }
            }
            return beServerListToReturn;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        // should never get here.
        return null;
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
        if(nodeName.equals("BEServer")) {
            BEServer.BEServerEntity BEServerEntityToAdd = new BEServer.BEServerEntity();
            BEServerEntityToAdd.setEntityFields(nodeName, host, pport, mport, ncores, java.util.Calendar.getInstance().getTimeInMillis());
            if(!BEServerList.contains(BEServerEntityToAdd)) {
                BEServerList.add(BEServerEntityToAdd);
            }
        } else if (nodeName.equals("FEServer")) {
            //System.out.println("TESTING " + nodeName + host+ pport+ mport+ncores);
            FEServer.FEServerEntity FEServerEntityToAdd = new FEServer.FEServerEntity();
            FEServerEntityToAdd.setEntityFields(nodeName, host, pport, mport, ncores);
            if(!FEServerList.contains(FEServerEntityToAdd)) {
                FEServerList.add(FEServerEntityToAdd);
            }
        }

        // clusterList.get(k).__debug_showInfo();

        System.out.println("[FEManagement] The [" + nodeName + "." + host + "." + pport + "." + mport + "." + ncores + "]" + " was added to the cluster");
        //System.out.println("BEServerList.size() = " + BEServerList.size());
        return true;
    }
}