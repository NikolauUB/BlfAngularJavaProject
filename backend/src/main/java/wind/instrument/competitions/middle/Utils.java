package wind.instrument.competitions.middle;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class Utils {

    public static String generateNewSalt () throws NoSuchAlgorithmException {
    }

    public static String encryptPassword(String password, String salt) throws NoSuchAlgorithmException {

    }

    /*public static boolean isEmailValid(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            result = false;
        }
        return result;
    }*/

}
