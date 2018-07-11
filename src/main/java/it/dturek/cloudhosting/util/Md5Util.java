package it.dturek.cloudhosting.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

    @SuppressWarnings("unused")
    private static final Logger LOGGER = LogManager.getLogger(Md5Util.class);

    public static String getHash(String string) {
        String hash = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes(), 0, string.length());
            hash = new BigInteger(1, md.digest(string.getBytes())).toString(16);
        } catch (NoSuchAlgorithmException e) {
            LOGGER.debug("Exception while making hash: {}", e);
        }
        return hash;
    }

}
