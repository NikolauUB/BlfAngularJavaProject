package wind.instrument.competitions.configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wind.instrument.competitions.middle.Utils;

import java.security.NoSuchAlgorithmException;

public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

    private static Logger LOG = LoggerFactory.getLogger(PasswordEncoder.class);

    @Override
    public String encode(CharSequence charSequence) {
        return null;
    }

    @Override
    public boolean matches(CharSequence charSequence, String hashAndSalt) {
        if (charSequence.length() == 0) {
            return true;
        }
        if (hashAndSalt == null) {
            return false;
        }
        String[] hashUndSalt = hashAndSalt.split(";");

        if (hashUndSalt.length != 2) {
            return false;
        }

        try {
            return hashUndSalt[0].equals(Utils.encryptPassword(charSequence.toString(), hashUndSalt[1]));
        } catch (NoSuchAlgorithmException e) {
            LOG.error("Error when check password: ", e);
        }

        return false;
    }
}
