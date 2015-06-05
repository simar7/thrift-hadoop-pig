import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;
import org.mindrot.jbcrypt.BCrypt;

// TODO: Check if this is the right excpetion
import java.lang.Exception;
import java.lang.System;

public class BEPasswordHandler implements BEPassword.Iface {
    
    private PerfCounters perfCounter = new PerfCounters();

    public BEPasswordHandler(PerfCounters perfCounter) {
        this.perfCounter = perfCounter;
    }

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        try {
            System.out.println("[BEPasswordHandler] Password = " + password + " " + "logRounds = " + logRounds);
            perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;
            String hashedString = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
            perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
            System.out.println("[BEPasswordHandler] hashedString = " + hashedString);
            if (hashedString.length() != 0) {
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
            perfCounter.numRequestsReceived = perfCounter.numRequestsReceived += 1;
            boolean result = BCrypt.checkpw(password, hash);
            if (result) {
                System.out.println("[BEPasswordHandler] Password Matches.");
                perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
                return true;
            }
            else {
                System.out.println("[BEPasswordHandler] Password Mismatch");
                perfCounter.numRequestsCompleted = perfCounter.numRequestsCompleted += 1;
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}