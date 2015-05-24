import A1.*;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class a1server {

    public static a1handler handler;
    public static a1service.Processor processor;

    public static void main(String [] args) {
        try {
            handler = new a1handler();
            processor = new a1service.Processor((a1service.Iface) handler);

            Runnable simple = new Runnable() {
                @Override
                public void run() {
                    simple(processor);
                }
            };

            new Thread(simple).start();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }

    public static void simple(a1service.Processor processor) {
        try {
            TServerTransport serverTransport = new TServerSocket(9090);
            TServer server = new TSimpleServer(new TServer.Args(serverTransport).processor(a1server.processor));

            System.out.println("Starting simple server");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

     
