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
import java.lang.Override;
import java.lang.Runnable;
import java.lang.String;
import java.lang.System;
import java.lang.Thread;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

public class FEServer {

    public static FEPasswordHandler handler_password;
    public static FEPassword.Processor processor_password;

    public static FEManagementHandler handler_management;
    public static FEManagement.Processor processor_management;

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

    public class BEServerEntity {
        public String nodeName;
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

        public String[] getBEHostNamePortNumber() {
            String[] ArrRet = {this.host, this.passwordPort.toString()};
            return ArrRet;
        }

        public String[] getBEHostNamePortNumberCores() {
            String[] ArrRet = {this.host, this.passwordPort.toString(), Integer.toString(this.numCores)};
            return ArrRet;
        }

        public int getBECores() {
            return this.numCores;
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

    public static CopyOnWriteArrayList<SeedEntity> seedEntityList = new CopyOnWriteArrayList<SeedEntity>();
    public static CopyOnWriteArrayList<BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServerEntity>();

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

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                System.out.print("Usage:");
                helpMenu();
            } else {
                FEServer.parseArgs(args);
            }

            //contactFESeed();

            handler_password = new FEPasswordHandler(BEServerList);
            processor_password = new FEPassword.Processor(handler_password);
            Runnable simple_password = new Runnable() {
                @Override
                public void run() {
                    simple_password(processor_password);
                }
            };
            new Thread(simple_password).start();

            // TODO: Do we actually need a Management port for Non-FE seeds?
            handler_management = new FEManagementHandler(seedEntityList, BEServerList);
            processor_management = new FEManagement.Processor(handler_management);
            Runnable simple_management = new Runnable() {
                @Override
                public void run() {
                    simple_management(processor_management);
                }
            };
            new Thread(simple_management).start();

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
            if (checkIfSeedOrNot(mport)) {
                // Experiment 1: TThreadedPoolServer
                TServerTransport serverTransport = new TServerSocket(mport);
                TServer server = new TThreadPoolServer(
                        new TThreadPoolServer.Args(serverTransport).processor(processor_management));
                server.serve();
            }

            // This is FEServer
            if (!checkIfSeedOrNot(mport)) {

                // Get a random seed from the seedEntityList to inform about arrival.
                SeedEntity seedToReach = seedEntityList.get(new Random().nextInt(seedEntityList.size()));

                // Part 1: FEServer connects to FESeed. (FEServer = client, FESeed = server)
                System.out.println("[FEServer] Requesting a BEServer from FESeed...");
                TTransport transport_management_seed;
                transport_management_seed = new TSocket(seedToReach.getSeedHostName(), seedToReach.getSeedPortNumber());
                transport_management_seed.open();


                TProtocol protocol_management_seed = new TBinaryProtocol(transport_management_seed);
                FEManagement.Client client_management_seed = new FEManagement.Client(protocol_management_seed);

                // chosenBEServer.get(0) = hostName of BEServer to reach to.
                // chosenBEServer.get(1) = PortNumber of BEServer to reach to.
                // chosenBEServer.get(2) = Cores of BEServer to reach to.
                List<String> chosenBEServer = new ArrayList<String>();
                chosenBEServer = requestBEServer(client_management_seed);

                //System.out.println("[FEServer] Chosen BEServer details: hostname =  " + chosenBEServer.get(0) + " | port = " + chosenBEServer.get(1));

                // Part 2: FEServer connects to BEServer. (FEServer = client, BEServer = server)

                // Instantiate the transport between FEServer and BEServer
                TTransport transport_password_beserver;
                System.out.println("[FEServer] Connecting to chosen BEServer to forward requests to...");
                transport_password_beserver = new TSocket(chosenBEServer.get(0), Integer.parseInt(chosenBEServer.get(1)));
                transport_password_beserver.open();

                // Instantiate the protocol between FEServer and FEPasswordHandler
                TProtocol protocol_password_beserver = new TBinaryProtocol(transport_password_beserver);
                //FEPassword.Client client_password_feserver = new FEPassword.Client(protocol_password_feserver);
                BEPassword.Client client_password_beserver = new BEPassword.Client(protocol_password_beserver);
                performPasswordRequestOnBE(chosenBEServer, client_password_beserver);
                transport_password_beserver.close();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Only executed if it's a FEServer. Not run for FESeeds.
    /*
        Upon receiving a request from client:
            1) Query the cluster list to find a BE.
            2) Forward request to BE.
    */

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

    private static List<String> requestBEServer(FEManagement.Client client_management_seed) throws TException {
        try {
            List<String> chosenBEServer = new ArrayList<String>();
            chosenBEServer = client_management_seed.getBEServer();
            return chosenBEServer;
        } catch (Exception e) {
            e.printStackTrace();
        }
        // should never get here.
        return null;
    }

    // TODO: Evaluate the need for this. Don't think we need this.
    // Only used if this is an actual FEServer and NOT a seed.
    private static void contactFESeed() throws TException {
        try {
            TTransport transport;
            // FIXME: 9999 is the FESeed for now. fix it to param
            transport = new TSocket("localhost", 9999);
            transport.open();

            // TODO : Parse args into a nice package before sending it to the FEManagementHandler.java
            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client_management = new FEManagement.Client(protocol);

            // FIXME: dont hardcode set it to its own port numbers
            boolean joinResult = client_management.joinCluster("FEServer", "localhost", 9090, 9091, 2);
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