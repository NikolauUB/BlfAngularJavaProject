package wind.instrument.competitions.configuration;


import wind.instrument.competitions.middle.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;


public class PasswordEncoder implements org.springframework.security.crypto.password.PasswordEncoder {

    private static Logger LOG = LoggerFactory.getLogger(PasswordEncoder.class);

    @Override
    public String encode(CharSequence charSequence) {
        return null;
    }

    @Override
    public boolean matches(CharSequence charSequence, String hashAndSalt) {
        if (hashAndSalt == null) {
            return false;
        }
        String[] hashUndSalt = hashAndSalt.split(";");

        if(hashUndSalt.length != 2) {
            return false;
        }

        try {
            return hashUndSalt[0].equals(Utils.encryptPassword(charSequence.toString(), hashUndSalt[1]));
        } catch (NoSuchAlgorithmException e) {
            LOG.debug("Error when check password: ", e );
        }

        return false;
    }
}
