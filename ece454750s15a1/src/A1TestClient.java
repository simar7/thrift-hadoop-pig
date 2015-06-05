import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.Integer;
import java.lang.System;
import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;


public class A1TestClient {

    public static String host;
    public static Integer pport;
    public static Integer mport;
    public static Integer ncores;
    public static String seed_string;

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
    public static List<SeedEntity> seedEntityList = new ArrayList<SeedEntity>();


    private static void helpMenu() {
        System.out.println("java ece454750s15a1.A1Client");
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
                        SeedEntity seed = new SeedEntity();
                        seed.setEntityFields(seedHostName, seedPortNumber);
                        seedEntityList.add(seed);
                    }
                }
            }
        }
    }

    public static void main(String [] args) {

        if (args.length == 0) {
            System.out.print("Usage:");
            helpMenu();
        }

        A1TestClient.parseArgs(args);
        while(true) {
            try {
                sendPass();
                //Thread.sleep(1000);
            }catch (Exception e) {
               e.printStackTrace();
            }
            
        }
    }
    
    private static void sendPass() {
        try{
                TTransport transport_password;
                System.out.println("Trying to start transport_password on pport = " + pport);
                transport_password = new TSocket("localhost", pport);
                transport_password.open();

                
                TTransport transport_management;
                System.out.println("Trying to start transport_management on mport = " + 11237);
                transport_management = new TSocket("localhost", 11237);
                transport_management.open();

                TTransport transport_fe_management;
                System.out.println("Trying to start transport_fe_management on mport = " + mport);
                transport_fe_management = new TSocket("localhost", mport);
                transport_fe_management.open();

                /*TTransport transport_be_password;
                System.out.println("Trying to start transport_be_password on mport = " + 11238);
                transport_be_password = new TSocket("localhost", 11238);
                transport_be_password.open();*/

                TProtocol protocol_password = new TBinaryProtocol(transport_password);
                TProtocol protocol_management = new TBinaryProtocol(transport_management);
                TProtocol protocol_fe_management = new TBinaryProtocol(transport_fe_management);

                FEPassword.Client client_password = new FEPassword.Client(protocol_password);
                BEManagement.Client client_management = new BEManagement.Client(protocol_management);
                FEManagement.Client client_fe_management = new FEManagement.Client(protocol_fe_management);

                perform_password(client_password);
                //System.out.println("perf performance");
                getBEPerfCounters(client_management);
                getFEPerfCounters(client_fe_management);

                // TODO: Implement better client logic to only close
                // when fully done.
                transport_password.close();
                transport_management.close();
                transport_fe_management.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    // This fucntion sends stuff to the servers via the handlers.
    private static void perform_password(FEPassword.Client client_password) throws TException {

        String passwordHash = client_password.hashPassword("password", (short) 10);
        System.out.println("Password Hash=" + passwordHash);

        boolean checkPassword = client_password.checkPassword("password", passwordHash);
        System.out.println("Check Password PASS=" + checkPassword);

        checkPassword = client_password.checkPassword("password_wrong", passwordHash);
        System.out.println("Check Password FAIL=" + checkPassword);
    }

    // Function to query performance counters
    private static void getBEPerfCounters(BEManagement.Client client_management_beserver) throws TException {
        //System.out.println(client_management_beserver);
        PerfCounters perfCounters = new PerfCounters();
        //String str = client_management_beserver.hashPassword("pas", (short) 10);
        //System.out.println("connection=" + str);
        perfCounters = client_management_beserver.getPerfCounters();

        System.out.println("[A1Client] ---- BE Performance Counters ----");
        System.out.println("[A1Client] Server uptime: " + perfCounters.numSecondsUp);
        System.out.println("[A1Client] Requests Rec : " + perfCounters.numRequestsReceived);
        System.out.println("[A1Client] Requesrs Com : " + perfCounters.numRequestsCompleted);
    }

    private static void getFEPerfCounters(FEManagement.Client client_management_feserver) throws TException {
        //System.out.println(client_management_beserver);
        PerfCounters perfCounters = new PerfCounters();
        //String str = client_management_beserver.hashPassword("pas", (short) 10);
        //System.out.println("connection=" + str);
        perfCounters = client_management_feserver.getPerfCounters();

        System.out.println("[A1Client] ---- FE Performance Counters ----");
        System.out.println("[A1Client] Server uptime: " + perfCounters.numSecondsUp);
        System.out.println("[A1Client] Requests Rec : " + perfCounters.numRequestsReceived);
        System.out.println("[A1Client] Requesrs Com : " + perfCounters.numRequestsCompleted);
    }
}
