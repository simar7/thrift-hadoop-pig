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

public class A1Client {

    public static String host;
    public static Integer pport;
    public static Integer mport;
    public static Integer ncores;
    public static String seed_string;
    public static List<String> seed_list;
    public static HashMap<Integer, String> seed_map = new HashMap<Integer, String>(100);


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
                    for (int j = 0; j < seeds_colon_delim.length - 1; j++) {
                        seed_map.put(Integer.parseInt(seeds_colon_delim[j + 1]), seeds_colon_delim[j]);
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

        A1Client.parseArgs(args);

        try {
            TTransport transport_password;
            System.out.println("Trying to start transport_password on pport = " + pport);
            transport_password = new TSocket("localhost", pport);
            transport_password.open();

            TTransport transport_management;
            System.out.println("Trying to start transport_management on mport = " + mport);
            transport_management = new TSocket("localhost", mport);
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
