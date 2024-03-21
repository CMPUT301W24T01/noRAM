/* Class to help with Hashing of strings. */
package com.example.noram.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class with methods to help with string hashing.
 * @maintainer Cole
 * @author Cole
 */
public class HashHelper {
    /**
     * Hash a string with SHA-256
     * @param input input string
     * @return hashed string
     */
    public static String hashSHA256(String input) {
        // Reference: https://stackoverflow.com/a/11009612, "How to hash some string with SHA-256 in Java", user1452273, accessed march 19 2024
        byte[] hashBytes;
        try {
            hashBytes = MessageDigest.getInstance("SHA-256").digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(hashByte & 0xff);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
