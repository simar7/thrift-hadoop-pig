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

    public static void main(String[] args) {
        try{
            handler_password = new FEPasswordHandler();
            processor_password = new FEPassword.Processor(handler_password);

            handler_management = new FEManagementHandler();
            processor_management = new FEManagement.Processor(handler_management);

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

            new Thread(simple_management).start();
            new Thread(simple_password).start();

            contactFESeed();

        } catch (Exception x) {
            x.printStackTrace();
        }
    }


    public static void simple_password(FEPassword.Processor processor_password) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor_password));

            System.out.println("Starting the simple password server...");
            server.serve();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void simple_management(FEManagement.Processor processor_management) {
        try {
            TServerTransport serverTransport = new TServerSocket(8090);
            TServer server = new TSimpleServer(
                    new Args(serverTransport).processor(processor_management));

            System.out.println("Starting the simple management server...");
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
            BEManagement.Client client_management = new BEManagement.Client(protocol);

            // TODO : Parse args into a nice package before sending it to the FEManagementHandler.java

            // FIXME: dont hardcode set it to its own port numbers
            boolean joinResult = client_management.joinCluster("localhost", 9090, 9091, 2);
            if(joinResult) {
                System.out.println("The FE Server was added to the cluster.");
            } else {
                System.out.println("The FE Server was NOT added to the cluster.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}