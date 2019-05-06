package com.bitlove.fetlife.nativeapp.util;

import com.bitlove.fetlife.nativeapp.model.pojos.fetlife.dbjson.Message;
import com.crashlytics.android.Crashlytics;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

public class MessageDuplicationDebugUtil {

    private static final int BODY_TRASHOLD = 3;

    private static String lastTypedHash, lastSentHash;

    public static boolean checkTypedMessage(String senderId, String body) {
        if (body == null || body.trim().length() <= BODY_TRASHOLD) {
            lastTypedHash = null;
            return false;
        }
        String hashBase = senderId + body;
        String hash = calculateHash(hashBase);

        boolean sameAsBefore = hash.equals(lastTypedHash);
        lastTypedHash = hash;

        return sameAsBefore;
    }

    public static boolean checkSentMessage(Message message) {
        String body = message.getBody();
        if (body == null || body.trim().length() <= BODY_TRASHOLD) {
            lastSentHash = null;
            return false;
        }
        String hashBase = message.getSenderId() + message.getBody();
        String hash = calculateHash(hashBase);

        boolean sameAsBefore = hash.equals(lastSentHash);
        lastSentHash = hash;

        if (sameAsBefore) {
            Crashlytics.logException(new Exception("Sent Message match; body length: " + body.length()));
        }

        return sameAsBefore;
    }

    private static String calculateHash(String hashBase) {
        try {
            return HashUtil.SHA1(hashBase);
        } catch (UnsupportedEncodingException|NoSuchAlgorithmException e) {
            return "";
        }
    }

}
