import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;
import org.mindrot.jbcrypt.BCrypt;

// TODO: Check if this is the right excpetion
import java.lang.Exception;
import java.lang.System;

public class BEPasswordHandler implements BEPassword.Iface {

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        System.out.println("[BEPasswordHandler] Password = " + password + " " + "logRounds = " + logRounds);

        String hashedString = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
        System.out.println("[BEPasswordHandler] hashedString = " + hashedString);
        if (hashedString.length() != 0) {
            return hashedString;
        }
        else {  // something isn't right.
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;
        }

    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        try {
            boolean result = BCrypt.checkpw(password, hash);
            if (result) {
                System.out.println("[BEPasswordHandler] Password Matches.");
                return true;
            }
            else {
                System.out.println("[BEPasswordHandler] Password Mismatch");
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}