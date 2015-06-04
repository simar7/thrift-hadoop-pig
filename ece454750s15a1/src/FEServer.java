import ece454750s15a1.*;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.*;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.NumberFormatException;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.System;
import java.lang.Thread;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FEServer {

    public static FEPasswordHandler handler_password;
    public static FEPassword.Processor processor_password;

    public static FEManagementHandler handler_management;
    public static FEManagement.Processor processor_management;

    public static FEManagementHandler handler_sync_with_seed;
    public static FEManagement.Processor processor_sync_with_seed;

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static String host;
    public static Integer pport;
    public static Integer mport;
    public static Integer ncores;
    public static String seed_string;

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

    public static CopyOnWriteArrayList<FEServer.SeedEntity> seedEntityList = new CopyOnWriteArrayList<FEServer.SeedEntity>();
    public static CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServer.BEServerEntity>();
    public static CopyOnWriteArrayList<FEServer.FEServerEntity> FEServerList = new CopyOnWriteArrayList<FEServer.FEServerEntity>();

    public static List<String> getAllSeedPorts() {
        List<String> seedPortList = new ArrayList<String>();
        for(int i = 0; i < seedEntityList.size(); i++) {
            //System.out.println("[FEDEBUG] seedportlist = " + Integer.toString(seedEntityList.get(i).getSeedPortNumber()));
            seedPortList.add(Integer.toString(seedEntityList.get(i).getSeedPortNumber()));
        }
        return seedPortList;
    }

    public static boolean checkIfSeedOrNot(Integer mport) {
        List<String> seedPortList = getAllSeedPorts();
        // System.out.println("mport = " + mport);

        if(seedPortList.contains(Integer.toString(mport))) {
            // System.out.println("its a seeeeed!");
            return true;
        }
        else {
            // System.out.println("its NOT a seeeeed!");
            return false;
        }
    }

    private static void helpMenu() {
        System.out.println("java ece454750s15a1.FEServer");
        System.out.println("-host: name of the host on which this process will run");
        System.out.println("-pport: port number for A1Password Service");
        System.out.println("-mport: port number for A1Management Service");
        System.out.println("-ncores: number of cores available to the process");
        System.out.println("-seeds: CSV list of host:port pairs in FE nodes that are seeds.");
        System.out.println("Seed ports are in A1Management service");
    }

    public static void parseArgs(String[] args) {
        for (int i = 0; i < args.length - 1; i++) {
            //System.out.println("[FEServer] args[" + i + "] = " + args[i]);
            String args_to_check = args[i];
            if (args_to_check.equals("-host")) {
                host = args[i + 1];
            } else if (args_to_check.equals("-pport")) {
                pport = Integer.parseInt(args[i + 1]);
            } else if (args_to_check.equals("-mport")) {
                mport = Integer.parseInt(args[i + 1]);
            } else if (args_to_check.equals("-ncores")) {
                ncores = Integer.parseInt(args[i + 1]);
            } else if (args_to_check.equals("-seeds")) {
                seed_string = args[i + 1];
                String[] seeds_comma_delim = seed_string.split(",");
                for (String seed_pair : seeds_comma_delim) {
                    String[] seeds_colon_delim = seed_pair.split(":");
                    for (int j = 0; j < seeds_colon_delim.length - 1; j = j + 2) {
                        String seedHostName = seeds_colon_delim[j];
                        int seedPortNumber = Integer.parseInt(seeds_colon_delim[j + 1]);
                        SeedEntity seed = new SeedEntity();
                        seed.setEntityFields(seedHostName, seedPortNumber);
                        seedEntityList.add(seed);
                    }
                }
            }
        }
    }

    public static void printCurrentListOfBEs(CopyOnWriteArrayList<BEServer.BEServerEntity> listOfBEs) {
        for (BEServer.BEServerEntity BEServerNode : listOfBEs) {
            BEServerNode.__debug_showInfo();
        }
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.print("Usage:");
                helpMenu();
            } else {
                FEServer.parseArgs(args);
            }

            // Uncomment this line to connect to FESeed.
            /*if (!checkIfSeedOrNot(mport)) {
                contactFESeed();
            }*/

            // TODO: Do we actually need a Management port for FEServers?
            handler_management = new FEManagementHandler(BEServerList, FEServerList);
            processor_management = new FEManagement.Processor(handler_management);
            Runnable simple_management = new Runnable() {
                @Override
                public void run() {
                    simple_management(processor_management);
                }
            };

            handler_password = new FEPasswordHandler(BEServerList);
            processor_password = new FEPassword.Processor(handler_password);
            Runnable simple_password = new Runnable() {
                @Override
                public void run() {
                    simple_password(processor_password);
                }
            };

            new Thread(simple_management).start();
            new Thread(simple_password).start();

            // Thread to sync FEServers with FESeed to get all the BEServer information.
            handler_sync_with_seed = new FEManagementHandler(BEServerList, FEServerList);
            processor_sync_with_seed = new FEManagement.Processor(handler_sync_with_seed);
            Runnable simple_sync_with_seed = new Runnable() {
                @Override
                public void run() {
                    simple_sync_with_seed(processor_sync_with_seed);
                }
            };

            if (checkIfSeedOrNot(mport)) {
                System.out.println("[FESeed] Running the sync_with_seed thread...");
            }
            else {
                System.out.println("[FEServer] Running the sync_with_seed thread...");
            }

            executor.scheduleAtFixedRate(simple_sync_with_seed, 0, 5000, TimeUnit.MILLISECONDS);
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple_management(FEManagement.Processor processor_management) {
        try {

            // This is FESeed
            // FIXME: Change the check condition from this to mport == seedlist.
            if (checkIfSeedOrNot(mport)) {
                System.out.println("[FESeed] Starting the FESeed management iface at mport = " + mport);
            } else { // TODO: Do we really need a management port for Non FESeed nodes?
                System.out.println("[FEServer] Starting the FEServer management iface at mport = " + mport);
            }

            // This is FESeed
            // Need this for other BEServers to register to the FESeed.
            if (checkIfSeedOrNot(mport)) {
                // Experiment 1: TThreadedPoolServer
                TServerTransport serverTransport = new TServerSocket(mport);
                TServer server = new TThreadPoolServer(
                        new TThreadPoolServer.Args(serverTransport).processor(processor_management));
                server.serve();
            }

            // This is FEServer
            if (!checkIfSeedOrNot(mport)) {
                // Some management logic for FEServer
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Only executed if it's a FEServer. Not run for FESeeds.
    public static void simple_password(FEPassword.Processor processor_password) {
        try {
            // This is FEServer
            if (!checkIfSeedOrNot(mport)) {
                // Part 0: Open a server socket to receive requests from the client.
                System.out.println("[FEServer] Now listening to client requests on pport = " + pport);

                // Experiment 1: TThreadedPoolServer
                TServerTransport serverTransport = new TServerSocket(pport);
                TServer server = new TThreadPoolServer(
                        new TThreadPoolServer.Args(serverTransport).processor(processor_password));

                server.serve();
            } else { // This is FESeed
                System.out.println("[FESeed] Password port in FESeed is not programmed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void simple_sync_with_seed(FEManagement.Processor processor_sync_with_seed) {
        try {
            // Get a random server to connect to each time.
            Random rand = new Random();
            int randomSeedIndex = rand.nextInt(seedEntityList.size());
            FEServer.SeedEntity seedEntity = seedEntityList.get(randomSeedIndex);

            // Connect to the randomly picked seed.
            // Only connect from this FE entity to another if it's different.
            if(seedEntity.getSeedPortNumber() != mport) {
                TTransport transport_sync_with_seed;
                if(checkIfSeedOrNot(mport)){
                    System.out.println("[FESeed] Gossiping with a seed on port = " + seedEntity.getSeedPortNumber());
                }
                else {
                    System.out.println("[FEServer] Gossiping with a seed on port = " + seedEntity.getSeedPortNumber());

                }
                transport_sync_with_seed = new TSocket(seedEntity.getSeedHostName(), seedEntity.getSeedPortNumber());
                transport_sync_with_seed.open();

                TProtocol protocol_sync_with_seed = new TBinaryProtocol(transport_sync_with_seed);
                FEManagement.Client client_management_sync_with_seed = new FEManagement.Client(protocol_sync_with_seed);

                List<String> newBEServersToAdd = client_management_sync_with_seed.getBEServerList();

                for (int i = 0; i < newBEServersToAdd.size(); i = i + 5) {
                    BEServer.BEServerEntity BEServerEntityToAdd = new BEServer.BEServerEntity(newBEServersToAdd.get(i), Integer.parseInt(newBEServersToAdd.get(i + 1)), Integer.parseInt(newBEServersToAdd.get(i + 2)), Integer.parseInt(newBEServersToAdd.get(i + 3)), newBEServersToAdd.get(i + 4));
                    if (!BEServerList.contains(BEServerEntityToAdd)) {
                        BEServerList.add(BEServerEntityToAdd);
                    }
                }

                // Print the updated list of members in the BEServerList
                if(checkIfSeedOrNot(mport)){
                    System.out.println("[FESeed] This FESeed is currently aware of [" + BEServerList.size() + "] BEServers");
                }
                else {
                    System.out.println("[FEServer] This FEServer is currently aware of [" + BEServerList.size() + "] BEServers");
                }

                transport_sync_with_seed.close();
            }
        }  catch (Exception e) {
            // We currently get here if the other seeds have not started yet...
            System.out.println("[FESeed] Waiting for other seeds in the system to start...");
            //e.printStackTrace();
        }
        // Add logic to catch ServiceUnavailableException.
    }

    // TODO: Evaluate the need for this. Don't think we need this.
    // Only used if this is an actual FEServer and NOT a seed.
    private static void contactFESeed() throws TException {
        try {
            Random rand = new Random();
            int randomSeedIndex = rand.nextInt(seedEntityList.size());
            FEServer.SeedEntity seedEntity = seedEntityList.get(randomSeedIndex);

            TTransport transport;
            transport = new TSocket(seedEntity.getSeedHostName(), seedEntity.getSeedPortNumber());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client_management = new FEManagement.Client(protocol);

            boolean joinResult = client_management.joinCluster("FEServer", "localhost", pport, mport, ncores);


            if (joinResult) {
                System.out.println("The FE Server was added to the cluster.");
            } else {
                System.out.println("The FE Server was NOT added to the cluster.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}