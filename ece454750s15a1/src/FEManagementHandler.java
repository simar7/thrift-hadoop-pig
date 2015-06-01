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

    private PerfCounters perfList;
    private List<String> hostList;
    private List<String> groupMembers;
    private CopyOnWriteArrayList<BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServerEntity>();

    public class BEServerEntity {
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

    public List<String> getTheBestPossibleBEServer() {
            int maxCoresFound = 0;
            String bestBEhostName = "";
            String bestBEPortNumber = "";

            /*  Uh... I don't know of a better way to do it.
                hostNamePortNumberCores[0] = hostName of BEServer
                hostNamePortNumberCores[1] = PortNumber of BEServer
                hostNamePortNumberCores[2] = Cores of BEServer
             */
            for(int node = 0; node < BEServerList.size(); node++) {
                String [] hostNamePortNumberCores = BEServerList.get(node).getBEHostNamePortNumberCores();
                // Simple core based logic to return the highest core'd BEServer.
                // TODO: Add logic based on timestamps when BEServer last joined.
                if (Integer.parseInt(hostNamePortNumberCores[2]) >= maxCoresFound) {
                    bestBEhostName = hostNamePortNumberCores[0];
                    bestBEPortNumber = hostNamePortNumberCores[1];
                    maxCoresFound = Integer.parseInt(hostNamePortNumberCores[2]);
                }
            }
        List<String> retList = new ArrayList<String>();
        // retList.addAll(Arrays.asList(bestBEhostName, bestBEPortNumber, Integer.toString(maxCoresFound)));
        Collections.addAll(retList, bestBEhostName, bestBEPortNumber, Integer.toString(maxCoresFound));
        return retList;
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
        BEServerEntity beServerEntity = new BEServerEntity();
        beServerEntity.setEntityFields(nodeName, host, pport, mport, ncores);
        BEServerList.add(beServerEntity);

        // clusterList.get(k).__debug_showInfo();

        System.out.println("[FESeed] The [" + nodeName + "]" + " was added to the cluster");
        return true;
    }

    public List<String> getBEServer() {
        // chosenBEServer[0] = hostName of BEServer
        // chosenBEServer[1] = PortNumber of BEServer
        // chosenBEServer[2] = Cores of BEServer

        List<String> chosenBEServer = getTheBestPossibleBEServer();
        return chosenBEServer;
    }

}