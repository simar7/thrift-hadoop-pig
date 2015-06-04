import ece454750s15a1.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class BEServer {

    public static BEPasswordHandler handler_password;
    public static BEPassword.Processor processor_password;

    public static BEManagementHandler handler_management;
    public static BEManagement.Processor processor_management;

    public static String host;
    public static Integer pport;
    public static Integer mport;
    public static Integer ncores;
    public static String seed_string;

    public static Long uptime;
    public static PerfCounters perfManager = new PerfCounters();

    public static class BEServerEntity {
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

        public BEServerEntity(String nodeName, Integer numCores, Integer passwordPort, Integer managementPort, String host) {
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

        @Override
        public boolean equals(final Object otherObj) {
            if((otherObj == null) || !(otherObj instanceof BEServerEntity)) {
                return false;
            }

            final BEServerEntity other = (BEServerEntity) otherObj;
            boolean equalStatus = false;

            equalStatus = (other.getBEPasswordPortNumber() == this.getBEPasswordPortNumber());
            return equalStatus;
        }

        public String[] getBEHostNamePortNumber() {
            String[] ArrRet = {this.host, this.passwordPort.toString()};
            return ArrRet;
        }

        public String[] getBEHostNamePortNumberCores() {
            String[] ArrRet = {this.host, this.passwordPort.toString(), Integer.toString(this.numCores)};
            return ArrRet;
        }

        public String getBENodeName() {return this.nodeName; }

        public String getBEHostName() {
            return this.host;
        }

        public int getBECores() {
            return this.numCores;
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
        public String  seedHostName;
        public Integer seedPort;

        public  SeedEntity() {
            this.seedHostName = "UNSET";
            this.seedPort = null;
        }

        public void setEntityFields(String seedHostName, int port) {
            this.seedHostName = seedHostName;
            this.seedPort = port;
        }

        public String [] getEntityFields() {
            String [] seedArrRet = {this.seedHostName, this.seedPort.toString()};
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
    public static List<FEServer.SeedEntity> seedEntityList = new ArrayList<FEServer.SeedEntity>();

    private static void helpMenu() {
        System.out.println("java ece454750s15a1.FEServer");
        System.out.println("-host: name of the host on which this process will run");
        System.out.println("-pport: port number for A1Password Service");
        System.out.println("-mport: port number for A1Management Service");
        System.out.println("-ncores: number of cores available to the process");
        System.out.println("-seeds: CSV list of host:port pairs in FE nodes that are seeds.");
        System.out.println("Seed ports are in A1Management service");
    }

    public static void parseArgs (String [] args) {
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
                    for (int j = 0; j < seeds_colon_delim.length - 1; j = j+2) {
                        String seedHostName = seeds_colon_delim[j];
                        int seedPortNumber = Integer.parseInt(seeds_colon_delim[j+1]);
                        FEServer.SeedEntity seed = new FEServer.SeedEntity();
                        seed.setEntityFields(seedHostName, seedPortNumber);
                        seedEntityList.add(seed);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try{

            handler_password = new BEPasswordHandler();
            processor_password = new BEPassword.Processor(handler_password);

            handler_management = new BEManagementHandler(perfManager, uptime);
            processor_management = new BEManagement.Processor(handler_management);

            if (args.length == 0) {
                System.out.print("Usage:");
                helpMenu();
            } else {
                System.out.println("[BEServer] Parsing args now..");
                BEServer.parseArgs(args);
            }

            Runnable simple_password = new Runnable() {
                @Override
                public void run() {
                    simple_password(processor_password);
                }
            };
            Runnable simple_management = new Runnable() {
                @Override
                public void run() {
                    simple_management(processor_management);
                }
            };

            uptime = System.currentTimeMillis();

            new Thread(simple_management).start();
            new Thread(simple_password).start();

            contactFESeed();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple_management(BEManagement.Processor processor_management) {
        try {
            TServerTransport serverTransport = new TServerSocket(mport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor_management));

            System.out.println("Starting the BEServer management iface at mport = " + mport);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple_password(BEPassword.Processor processor_password) {
        try {
            TServerTransport serverTransport = new TServerSocket(pport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor_password));

            System.out.println("Starting the BEServer password iface at pport = " + pport);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This fucntion sends stuff to the servers via the handlers.
    private static void contactFESeed() throws TException {
        try {
            TTransport transport;
            // Get a random seed from the seedEntityList to inform about arrival.
            FEServer.SeedEntity seedToReach = seedEntityList.get(new Random().nextInt(seedEntityList.size()));

            transport = new TSocket(seedToReach.getSeedHostName(), seedToReach.getSeedPortNumber());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client_management = new FEManagement.Client(protocol);

            boolean joinResult = client_management.joinCluster("BEServer", "localhost", pport, mport, ncores);
            if(joinResult) {
                System.out.println("[BEServer] Successfully added BEServer to cluster.");
            } else {
                System.out.println("[BEServer] Failed to add BEServer to cluster.");
            }

            transport.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}