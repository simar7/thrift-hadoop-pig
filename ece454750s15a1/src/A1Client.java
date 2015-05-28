import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import java.lang.Integer;
import java.lang.System;

public class A1Client {

    private static String host;
    private static Integer pport;
    private static Integer mport;
    private static Integer ncores;
    // TODO: fix this to be more than one, csv'd by comma and colon.
    private static String seeds;

    private static void helpMenu() {
        System.out.println("java ece454750s15a1.FEServer");
        System.out.println("-host: name of the host on which this process will run");
        System.out.println("-pport: port number for A1Password Service");
        System.out.println("-mport: port number for A1Management Service");
        System.out.println("-ncores: number of cores available to the process");
        System.out.println("-seeds: CSV list of host:port pairs in FE nodes that are seeds.");
        System.out.println("Seed ports are in A1Management service");
    }


    private static void parseArgs (String [] args) {
        for(int i = 0; i < args.length - 1; i++) {
            String args_to_check = args[i];
                if(args_to_check == "-host") {
                    host = args[i + 1];
                }
                else if(args_to_check == "-pport") {
                    pport = Integer.parseInt(args[i + 1]);
                }
                else if(args_to_check == "-mport") {
                    mport = Integer.parseInt(args[i + 1]);
                }
                else if(args_to_check == "ncores") {
                    ncores = Integer.parseInt(args[i + 1]);
                }
                else if(args_to_check == "seeds") {
                    seeds = args[i + 1];
                }
        }
    }


    public static void main(String [] args) {

        if (args.length == 0) {
            System.out.print("Usage:");
            helpMenu();
        }

        parseArgs(args);

        try {
            TTransport transport_password;
            transport_password = new TSocket("localhost", 9090);
            transport_password.open();

            TTransport transport_management;
            transport_management = new TSocket("localhost", 8090);
            transport_management.open();

            TProtocol protocol_password = new TBinaryProtocol(transport_password);
            TProtocol protocol_management = new TBinaryProtocol(transport_management);

            BEPassword.Client client_password = new BEPassword.Client(protocol_password);
            BEManagement.Client client_management = new BEManagement.Client(protocol_management);

            perform_password(client_password);
            //perform_management(client_management);

            // TODO: Implement better client logic to only close
            // when fully done.
            transport_password.close();
            transport_management.close();

        } catch (TException e) {
            e.printStackTrace();
        }
    }

    // This fucntion sends stuff to the servers via the handlers.
    private static void perform_password(BEPassword.Client client_password) throws TException {

        String passwordHash = client_password.hashPassword("password", (short) 10);
        System.out.println("Password Hash=" + passwordHash);

        boolean checkPassword = client_password.checkPassword("password", passwordHash);
        System.out.println("Check Password PASS=" + checkPassword);

        checkPassword = client_password.checkPassword("password_wrong", passwordHash);
        System.out.println("Check Password FAIL=" + checkPassword);
    }
}
