import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;
import org.mindrot.jbcrypt.BCrypt;

// TODO: Check if this is the right excpetion
import java.lang.Exception;

public class A1PasswordHandler implements A1Password.Iface {

    public A1PasswordHandler() {
        // TODO: Do we need anything here?
    }

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {

        String hashedString = BCrypt.hashpw(password, BCrypt.gensalt(logRounds));
        if (hashed) {
            return hashedString;
        }
        else {  // something isn't right.
            ServiceUnavailableException SUE = new ServiceUnavailableException();
            throw SUE;
        }

    }

    public boolean checkPassword(String password, String hash) throws org.apache.thrift.TException {
        try {
            // TODO: Implement jBcrypt logic.
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}