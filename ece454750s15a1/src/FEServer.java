import ece454750s15a1.*;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.TException;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.Exception;
import java.lang.Override;
import java.lang.Runnable;
import java.lang.Thread;

public class FEServer {

    public static FEPasswordHandler handler_password;
    public static FEPassword.Processor processor_password;

    public static FEManagementHandler handler_management;
    public static FEManagement.Processor processor_management;

    public static String host;
    public static Integer pport;
    public static Integer mport;
    public static Integer ncores;
    // TODO: fix this to be more than one, csv'd by comma and colon.
    public static String seeds;

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
        for(int i = 0; i < args.length - 1; i++) {
            //System.out.println("[feserver] args[" + i + "] = " + args[i]);
            String args_to_check = args[i];
            if(args_to_check.equals("-host")) {
                host = args[i + 1];
            }
            else if(args_to_check.equals("-pport")) {
                pport = Integer.parseInt(args[i + 1]);
            }
            else if(args_to_check.equals("-mport")) {
                mport = Integer.parseInt(args[i + 1]);
            }
            else if(args_to_check.equals("-ncores")) {
                ncores = Integer.parseInt(args[i + 1]);
            }
            else if(args_to_check.equals("-seeds")) {
                seeds = args[i + 1];
            }
        }
    }

    public static void main(String[] args) {
        try{
            if (args.length == 0) {
                System.out.print("Usage:");
                helpMenu();
            } else {
                FEServer.parseArgs(args);
            }

            //contactFESeed();


            // If this FEServer not a seed.
            if (pport != null) {
                handler_password = new FEPasswordHandler();
                processor_password = new FEPassword.Processor(handler_password);
                Runnable simple_password = new Runnable() {
                    @Override
                    public void run() {
                        simple_password(processor_password);
                    }
                };
                new Thread(simple_password).start();
            }

            handler_management = new FEManagementHandler();
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

    // Only executed if it's a FEServer. Not run for FESeeds.
    public static void simple_password(FEPassword.Processor processor_password) {
        try {
            TServerTransport serverTransport = new TServerSocket(pport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor_password));

            System.out.println("Starting the FEServer password iface at pport = " + pport);
            server.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple_management(FEManagement.Processor processor_management) {
        try {
            TServerTransport serverTransport = new TServerSocket(mport);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor_management));

            System.out.println("[FEServer] Starting the FEServer management iface at mport = " + mport);
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
            boolean joinResult = client_management.joinCluster("localhost", 9090, 9091, 2);
            if(joinResult) {
                System.out.println("The FE Server was added to the cluster.");
            } else {
                System.out.println("The FE Server was NOT added to the cluster.");
            }

        } catch (Exception e) {
            e.getLocalizedMessage();
            e.printStackTrace();
        }

    }
}