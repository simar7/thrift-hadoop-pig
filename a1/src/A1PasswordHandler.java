import org.apache.thrift.TException;
import org.mindrot.jbcrypt.*;

// Generated code
import ece454750s15a1.*;

// TODO: Check if this is the right excpetion
import java.lang.Exception;

public class A1PasswordHandler implements A1Password.Iface {

    public A1PasswordHandler() {
        // TODO: Do we need anything here?
    }

    public String hashPassword(String password, short logRounds) throws ServiceUnavailableException {
        // TODO: jBcrypt logic here.
        // TODO: Fix the empty string to meaningful hash.
        // return "";

        // if something isn't right
        ServiceUnavailableException SUE = new ServiceUnavailableException();
        throw SUE;
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