import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

// TODO: Check if this is the right excpetion
import java.lang.Exception;
import java.lang.Integer;
import java.lang.System;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Random;


public class FEPasswordHandler implements FEPassword.Iface {

    private CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList = null;
    private PerfCounters perfCounter = new PerfCounters();

    public FEPasswordHandler(CopyOnWriteArrayList<BEServer.BEServerEntity> BEServerList, PerfCounters perfCounter) {
        this.BEServerList = BEServerList;
        this.perfCounter = perfCounter;
    }

    public BEServer.BEServerEntity getTheHighestCoreServer() {
        int maxCoresFound = 0;
        BEServer.BEServerEntity chosenBEServer = null;

        System.out.println("[FEPasswordHandler] Picking a BEServer based on highest ncores ONLY..");

        // This can be optimized to a binary search.
        // IFF we ordered our CopyOnWriteArrayList by ncores order.
        for (int node = 0; node < this.BEServerList.size(); node++) {
            // Simple core based logic to return the highest core'd BEServer.
            // this.BEServerList.get(node).__debug_showInfo();
            if (this.BEServerList.get(node).getBECores() >= maxCoresFound) {
                chosenBEServer = this.BEServerList.get(node);
                maxCoresFound = this.BEServerList.get(node).getBECores();
            }
        }

        return chosenBEServer;
    }

    // We belive that Most recently joined BEServer
    // would be the Least Recently Used BEServer.
    public BEServer.BEServerEntity getTheLRUBEServer() {
        Long newestTime = Long.MAX_VALUE;
        BEServer.BEServerEntity chosenBEServer = null;

        System.out.println("[FEPasswordHandler] Picking a BEServer based on the most recently joined..");

        for (int node = 0; node < this.BEServerList.size(); node++) {
            if (this.BEServerList.get(node).getBEJoinTime() <= newestTime) {
                System.out.println("a new beserver was found!");
                chosenBEServer = this.BEServerList.get(node);
                newestTime = this.BEServerList.get(node).getBEJoinTime();
            }
        }

        return chosenBEServer;
    }

    public BEServer.BEServerEntity getRandomBEServer() {
        System.out.println("[FEPasswordHandler] Picking a random BEServer..");

        Random rand = new Random();
        int beserverindex = rand.nextInt(BEServerList.size());
        BEServer.BEServerEntity chosenBEServer = this.BEServerList.get(beserverindex);
        return chosenBEServer;
    }

    public BEServer.BEServerEntity getCoredRandomBEServer() {
        System.out.println("[FEPasswordHandler] Picking a random BEServer..");
        int totalWeight = 0;
        int randomIndex = 0;

        Random rand = new Random(System.currentTimeMillis());

        for (int i=0; i < BEServerList.size(); i++) {
            totalWeight += BEServerList.get(i).getBECores();
        }

        randomIndex = rand.nextInt(totalWeight);

        for (int i=0; i < BEServerList.size(); i++) {
            if(randomIndex < BEServerList.get(i).getBECores()) return BEServerList.get(i);
            randomIndex -= BEServerList.get(i).getBECores();
        }

        return BEServerList.get(randomIndex);
    }
    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        while (BEServerList.size() != 0) {
            try {
                String hashedPassword = null;

                System.out.println("[FEPasswordHandler] Hashing Password...");
                System.out.println("[FEPasswordHandler] Password = " + password + " " + "logRounds = " + logRounds);
                perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;

                // BEServer.BEServerEntity chosenBEServer = getTheHighestCoreServer();
                // BEServer.BEServerEntity chosenBEServer = getTheLRUBEServer();
                // BEServer.BEServerEntity chosenBEServer = getRandomBEServer();
                BEServer.BEServerEntity chosenBEServer = getCoredRandomBEServer();

                TTransport transport_password_fepassword;
                transport_password_fepassword = new TSocket(chosenBEServer.getBEHostName(), chosenBEServer.getBEPasswordPortNumber());
                transport_password_fepassword.open();
                TProtocol protocol = new TBinaryProtocol(transport_password_fepassword);
                BEPassword.Client client = new BEPassword.Client(protocol);

                hashedPassword = client.hashPassword(password, logRounds);
                transport_password_fepassword.close();

                System.out.println("[FEPasswordHandler] hashedPassword = " + hashedPassword);
                perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
                return hashedPassword;

            } catch (Exception e) {  // Oh noez! The BEServer has crashed!
                // e.printStackTrace();
                System.out.println("[FEPasswordHandler] Re-routing hashPassword request...");
                try {
                    //Thread.sleep(100);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
        if (BEServerList.size() == 0) {
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;
        }
        // should never get here
        return null;
    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        while (BEServerList.size() != 0) {
            try {
                //Random rand = new Random();
                //int randomBEServerIndex = rand.nextInt(BEServerList.size());

                // BEServer.BEServerEntity chosenBEServer = getTheHighestCoreServer();
                // BEServer.BEServerEntity chosenBEServer = getTheLRUBEServer();
                BEServer.BEServerEntity chosenBEServer = getRandomBEServer();

                TTransport transport_password_fepassword;
                transport_password_fepassword = new TSocket(chosenBEServer.getBEHostName(), chosenBEServer.getBEPasswordPortNumber());
                transport_password_fepassword.open();
                TProtocol protocol = new TBinaryProtocol(transport_password_fepassword);
                BEPassword.Client client = new BEPassword.Client(protocol);

                System.out.println("[FEPasswordHandler] Checking Password: " + password);
                perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;

                boolean result = client.checkPassword(password, hash);
                if (result) {
                    System.out.println("[FEPasswordHandler] Password Match.");
                    transport_password_fepassword.close();
                    perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
                    return true;
                } else {
                    System.out.println("[FEPasswordHandler] Password Does Not Match.");
                    transport_password_fepassword.close();
                    perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
                    return false;
                }
            } catch (Exception e) {  // Oh noez! The BEServer has crashed!
                // e.printStackTrace();
                System.out.println("[FEPasswordHandler] Re-routing hashPassword request...");
                try {
                    //Thread.sleep(100);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        }
        if (BEServerList.size() == 0) {
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;
        }
        return false;
    }
}