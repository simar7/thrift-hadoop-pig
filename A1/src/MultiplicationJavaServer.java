import org.apache.thrift.server.Tserver;
import org.apache.thrift.server.Tserver.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;

public class MultiplicationServer {
    
    public static MultiplicationHandler handler;
    public static MultiplicationService.Processor processor;

     
