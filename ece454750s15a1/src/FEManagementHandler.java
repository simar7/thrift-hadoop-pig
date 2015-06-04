import ece454750s15a1.*;

import org.apache.thrift.TException;

import java.lang.*;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.NumberFormatException;
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

    private CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList = null;
    private CopyOnWriteArrayList<FEServer.SeedEntity> seedList = new CopyOnWriteArrayList<FEServer.SeedEntity>();

    public FEManagementHandler(CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList) {
        this.BEServerList = BEServerList;
        //this.seedList = seedList;
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
        BEServer.BEServerEntity BEServerEntityToAdd = new BEServer.BEServerEntity();
        BEServerEntityToAdd.setEntityFields(nodeName, host, pport, mport, ncores);
        if(!BEServerList.contains(BEServerEntityToAdd)) {
            BEServerList.add(BEServerEntityToAdd);
        }

        // clusterList.get(k).__debug_showInfo();

        System.out.println("[FEManagement] The [" + nodeName + "." + host + "." + pport + "." + mport + "." + ncores + "]" + " was added to the cluster");
        //System.out.println("BEServerList.size() = " + BEServerList.size());
        return true;
    }
}