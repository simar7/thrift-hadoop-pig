import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;
import org.mindrot.jbcrypt.BCrypt;

// TODO: Check if this is the right excpetion
import java.lang.Exception;
import java.lang.System;

public class BEPasswordHandler implements BEPassword.Iface {
    
    private PerfCounters perfCounter = new PerfCounters();
    BEManagementHandler handler;

    public BEPasswordHandler(BEManagementHandler handler) {
        this.handler = handler;
    }

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        try {
            handler.perfCounter.numRequestsReceived++;
            System.out.println("[BEPasswordHandler] Password = " + password + " " + "logRounds = " + logRounds);

            String hashedString = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
            System.out.println("[BEPasswordHandler] hashedString = " + hashedString);
            if (hashedString.length() != 0) {
                handler.perfCounter.numRequestsCompleted++;
                return hashedString;
            }
        }
        catch (Exception e){  // something isn't right.
            e.printStackTrace();
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;
        }
        // should not reach this.
        return null;
    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        try {
            handler.perfCounter.numRequestsReceived++;
            boolean result = BCrypt.checkpw(password, hash);
            if (result) {
                System.out.println("[BEPasswordHandler] Password Matches.");
                handler.perfCounter.numRequestsCompleted++;
                return true;
            }
            else {
                System.out.println("[BEPasswordHandler] Password Mismatch");
                handler.perfCounter.numRequestsCompleted++;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}