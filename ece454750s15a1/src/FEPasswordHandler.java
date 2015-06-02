import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

// TODO: Check if this is the right excpetion
import java.lang.Exception;
import java.lang.System;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Random;

public class FEPasswordHandler implements FEPassword.Iface {

    public class BEServerEntity {
        public String nodeName;
        public Integer numCores;
        public String host;
        public Integer passwordPort;
        public Integer managementPort;

        public BEServerEntity() {
            this.nodeName = "UNSET";
            this.numCores = null;
            this.host = "UNSET";
            this.passwordPort = null;
            this.managementPort = null;
        }

        public void setEntityFields(String nodeName, String host, int pport, int mport, int numCores) {
            this.nodeName = nodeName;
            this.numCores = numCores;
            this.passwordPort = pport;
            this.managementPort = mport;
            this.host = host;
        }

        public String[] getBEHostNamePortNumber() {
            String[] ArrRet = {this.host, this.passwordPort.toString()};
            return ArrRet;
        }

        public String[] getBEHostNamePortNumberCores() {
            String[] ArrRet = {this.host, this.passwordPort.toString(), Integer.toString(this.numCores)};
            return ArrRet;
        }

        public int getBECores() {
            return this.numCores;
        }

        public String getBEHostName() {
            return this.host;
        }

        public int getBEManagementPortNumber() {
            return this.managementPort;
        }

        public int getBEPasswordPortNumber() {
            return this.passwordPort;
        }

        public void __debug_showInfo() {
            System.out.println("nodeName = " + this.nodeName);
            System.out.println("host = " + this.host);
            System.out.println("pport = " + this.passwordPort);
            System.out.println("mport = " + this.managementPort);
            System.out.println("numCores = " + this.numCores);
        }

    }

    private CopyOnWriteArrayList<BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServerEntity>();

    public FEPasswordHandler(CopyOnWriteArrayList<BEServerEntity> BEServerList) {
        this.BEServerList = BEServerList;
    }

    public BEServerEntity getTheBestPossibleBEServer() {
        int maxCoresFound = 0;
        BEServerEntity chosenBEServer = null;

        for(int node = 0; node < BEServerList.size(); node++) {
            // Simple core based logic to return the highest core'd BEServer.
            // TODO: Add logic based on timestamps when BEServer last joined.
            if (BEServerList.get(node).getBECores() >= maxCoresFound) {
                chosenBEServer = BEServerList.get(node);
            }
        }
        return chosenBEServer;
    }

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        String hashedPassword = null;

        System.out.println("[FEPasswordHandler] Hashing Password...");
        System.out.println("[FEPasswordHandler] Password = " + password + " " + "logRounds = " + logRounds);

        //Random rand = new Random();
        //int randomBEServerIndex = rand.nextInt(BEServerList.size());

        BEServerEntity chosenBEServer = getTheBestPossibleBEServer();

        TTransport transport_password_fepassword;
        transport_password_fepassword = new TSocket(chosenBEServer.getBEHostName(), chosenBEServer.getBEPasswordPortNumber());
        transport_password_fepassword.open();
        TProtocol protocol = new TBinaryProtocol(transport_password_fepassword);
        BEPassword.Client client = new BEPassword.Client(protocol);

        hashedPassword = client.hashPassword(password, logRounds);
        transport_password_fepassword.close();

        else{  // something isn't right.
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;  // FIXME: Throw the proper exception.
        }

        System.out.println("[FEPasswordHandler] hashedPassword = " + hashedPassword);
        return hashedPassword;
    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        try {
            //Random rand = new Random();
            //int randomBEServerIndex = rand.nextInt(BEServerList.size());

            BEServerEntity chosenBEServer = getTheBestPossibleBEServer();

            TTransport transport_password_fepassword;
            transport_password_fepassword = new TSocket(chosenBEServer.getBEHostName(), chosenBEServer.getBEPasswordPortNumber());
            transport_password_fepassword.open();
            TProtocol protocol = new TBinaryProtocol(transport_password_fepassword);
            BEPassword.Client client = new BEPassword.Client(protocol);

            System.out.println("[FEPasswordHandler] Checking Password: " + password);

            boolean result = client.checkPassword(password, hash);
            if (result) {
                System.out.println("[FEPasswordHandler] Password Match.");
                transport_password_fepassword.close();
                return true;
            } else {
                System.out.println("[FEPasswordHandler] Password Does Not Match.");
                transport_password_fepassword.close();
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}