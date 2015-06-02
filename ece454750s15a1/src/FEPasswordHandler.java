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

    public static CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList = new CopyOnWriteArrayList<BEServer.BEServerEntity>();

    public FEPasswordHandler(CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList) {
        this.BEServerList = BEServerList;
    }

    public BEServer.BEServerEntity getTheBestPossibleBEServer() {
        int maxCoresFound = 0;
        BEServer.BEServerEntity chosenBEServer = null;

        for(int node = 0; node < this.BEServerList.size(); node++) {
            // Simple core based logic to return the highest core'd BEServer.
            // TODO: Add logic based on timestamps when BEServer last joined.
            System.out.println("picking node based on ncores");
            this.BEServerList.get(node).__debug_showInfo();
            if (this.BEServerList.get(node).getBECores() >= maxCoresFound) {
                chosenBEServer = this.BEServerList.get(node);
            }
        }
        return chosenBEServer;
    }

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        try {
            String hashedPassword = null;

            System.out.println("[FEPasswordHandler] Hashing Password...");
            System.out.println("[FEPasswordHandler] Password = " + password + " " + "logRounds = " + logRounds);

            //Random rand = new Random();
            //int randomBEServerIndex = rand.nextInt(BEServerList.size());

            BEServer.BEServerEntity chosenBEServer = getTheBestPossibleBEServer();


            TTransport transport_password_fepassword;
            transport_password_fepassword = new TSocket(chosenBEServer.getBEHostName(), chosenBEServer.getBEPasswordPortNumber());
            transport_password_fepassword.open();
            TProtocol protocol = new TBinaryProtocol(transport_password_fepassword);
            BEPassword.Client client = new BEPassword.Client(protocol);

            hashedPassword = client.hashPassword(password, logRounds);
            transport_password_fepassword.close();

            System.out.println("[FEPasswordHandler] hashedPassword = " + hashedPassword);
            return hashedPassword;

        }
        catch (Exception e){  // something isn't right.
            e.printStackTrace();
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;  // FIXME: Throw the proper exception.
        }
    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        try {
            //Random rand = new Random();
            //int randomBEServerIndex = rand.nextInt(BEServerList.size());

            BEServer.BEServerEntity chosenBEServer = getTheBestPossibleBEServer();

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