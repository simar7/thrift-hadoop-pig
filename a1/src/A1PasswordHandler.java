import org.apache.thrift.TException;

// Generated code
import ece454750s15a1.*;

// TODO: Check if this is the right excpetion
import javax.naming.ServiceUnavailableException;

public class A1PasswordHandler implements A1Password.Iface {

    public A1PasswordHandler() {
        // TODO: Do we need anything here?
    }

    String hashPassword(String password, int logRounds) throws ServiceUnavailableException {
        // TODO: jBcrypt logic here.
    }
}