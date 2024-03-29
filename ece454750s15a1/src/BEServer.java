import ece454750s15a1.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TNonblockingServer;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.*;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TNonblockingServerTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.thrift.TProcessorFactory;


import java.lang.*;
import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.Calendar;

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
    public static boolean hasBEServerConnectedToSeed = false;
    public static PerfCounters perfManager = new PerfCounters();

    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static class BEServerEntity {
        public String nodeName;
        public Integer numCores;
        public String host;
        public Integer passwordPort;
        public Integer managementPort;
        public Long joinTime;

        public BEServerEntity() {
            this.nodeName = "UNSET";
            this.numCores = null;
            this.host = "UNSET";
            this.passwordPort = null;
            this.managementPort = null;
            this.joinTime = null;
        }

        public BEServerEntity(String nodeName, Integer numCores, Integer passwordPort, Integer managementPort, String host) {
            this.nodeName = nodeName;
            this.numCores = numCores;
            this.passwordPort = passwordPort;
            this.managementPort = managementPort;
            this.host = host;
            this.joinTime = System.currentTimeMillis();
        }

        public void setEntityFields(String nodeName, String host, int pport, int mport, int numCores, Long joinTime) {
            this.nodeName = nodeName;
            this.numCores = numCores;
            this.passwordPort = pport;
            this.managementPort = mport;
            this.host = host;
            this.joinTime = joinTime;
        }

        @Override
        public boolean equals(final Object otherObj) {
            if ((otherObj == null) || !(otherObj instanceof BEServerEntity)) {
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

        public String getBENodeName() {
            return this.nodeName;
        }

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

        public Long getBEJoinTime() {
            return this.joinTime;
        }

        public void __debug_showInfo() {
            System.out.println("nodeName = " + this.nodeName);
            System.out.println("host = " + this.host);
            System.out.println("pport = " + this.passwordPort);
            System.out.println("mport = " + this.managementPort);
            System.out.println("numCores = " + this.numCores);
            System.out.println("joinTime = " + this.joinTime);
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
                        FEServer.SeedEntity seed = new FEServer.SeedEntity();
                        seed.setEntityFields(seedHostName, seedPortNumber);
                        seedEntityList.add(seed);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {

            uptime = System.currentTimeMillis();

            handler_management = new BEManagementHandler(perfManager, uptime);
            processor_management = new BEManagement.Processor(handler_management);

            handler_password = new BEPasswordHandler(perfManager);
            processor_password = new BEPassword.Processor(handler_password);

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
            Runnable contactFESeed = new Runnable() {
                @Override
                public void run() {
                    contactFESeed();
                }
            };

            new Thread(simple_management).start();
            new Thread(simple_password).start();

            // contact other FESeeds to notify about presence.
            // periodic check is needed in case FESeeds go down.
            executor.scheduleAtFixedRate(contactFESeed, 0, 50, TimeUnit.MILLISECONDS);

            //new Thread(contactFESeed).start();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple_management(BEManagement.Processor processor_management) {
        try {
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(mport);
            TServer server = new TNonblockingServer(
                    new TNonblockingServer.Args(serverTransport).processor(processor_management));

            System.out.println("Starting the BEServer management iface at mport = " + mport);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple_password(BEPassword.Processor processor_password) {
        try {
            TServerTransport serverTransport = new TServerSocket(pport);

            TThreadPoolServer.Args args = new TThreadPoolServer.Args(serverTransport);
            args.protocolFactory(new TBinaryProtocol.Factory());
            args.transportFactory(new TTransportFactory());
            args.processorFactory(new TProcessorFactory(processor_password));
            args.minWorkerThreads(ncores);

            TThreadPoolServer server = new TThreadPoolServer(args);

            System.out.println("Starting the BEServer password iface at pport = " + pport);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This fucntion sends stuff to the servers via the handlers.
    private static void contactFESeed() {
        try {
            TTransport transport;
            // Get a random seed from the seedEntityList to inform about arrival.
            FEServer.SeedEntity seedToReach = seedEntityList.get(new Random().nextInt(seedEntityList.size()));

            transport = new TSocket(seedToReach.getSeedHostName(), seedToReach.getSeedPortNumber());
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client_management = new FEManagement.Client(protocol);

            boolean joinResult = client_management.joinCluster("BEServer", "localhost", pport, mport, ncores);
            if (joinResult) {
                System.out.println("[BEServer] Successfully added BEServer to cluster.");
            } else {
                System.out.println("[BEServer] Failed to add BEServer to cluster.");
            }

            //hasBEServerConnectedToSeed = true;
            transport.close();

        } catch (Exception e) {
            // Requested FESeed hasn't come up yet...
            //e.printStackTrace();
            System.out.println("[BEServer] Waiting for FESeed to connect to...");
            try {
                Thread.sleep(100);
            } catch (Exception x) {
                // should never really happen..
                x.printStackTrace();
            }
        }
    }
}