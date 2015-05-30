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

public class BEServer {

    public static BEPasswordHandler handler_password;
    public static BEPassword.Processor processor_password;

    public static BEManagementHandler handler_management;
    public static BEManagement.Processor processor_management;

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
            //System.out.println("[beserver] args[" + i + "] = " + args[i]);
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

            handler_password = new BEPasswordHandler();
            processor_password = new BEPassword.Processor(handler_password);

            handler_management = new BEManagementHandler();
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

            contactFESeed();

            new Thread(simple_management).start();
            new Thread(simple_password).start();


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
            // FIXME: 9999 is the FESeed for now. fix it to param
            transport = new TSocket("localhost", 9999);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            FEManagement.Client client_management = new FEManagement.Client(protocol);

            boolean joinResult = client_management.joinCluster("localhost", pport, mport, ncores);
            if(joinResult) {
                System.out.println("[BEServer] Successfully added BEServer to cluster.");
            } else {
                System.out.println("[BEServer] Failed to add BEServer to cluster.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}