import ece454750s15a1.*;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

public class A1Client {

    private void helpMenu() {
        System.out.println("java ece454750s15a1.FEServer");
        System.out.println("-host: name of the host on which this process will run");
        System.out.println("-pport: port number for A1Password Service");
        System.out.println("-mport: port number for A1Management Service");
        System.out.println("-ncores: number of cores available to the process"):
        System.out.println("-seeds: CSV list of host:port pairs in FE nodes that are seeds.");
        System.out.println("Seed ports are in A1Management service");
    }

    public static void main(String [] args) {

        if (args.length == 0) {
            System.out.print("Usage:");
            helpMenu();
        }

        // The FEServer is the Server for the A1Client.
        try {
            TTransport transport;
            transport = new TSocket("localhost", 9090);
            transport.open();

            TProtocol protocol = new TBinaryProtocol(transport);
            A1PasswordHandler.Client client = new A1Password.Client(protocol);

            perform(client);

            // TODO: Implement better client logic to only close
            // when fully done.
            transport.close();
        } catch (TException e) {
            e.printStackTrace();
        }
    }

    private static void perform(A1PasswordHandler.Client client) throws TException {

        // TODO: Finish up taking inputs from CLI.
        System.out.print("Enter password: ");
    }
}