package top.iceclean.chatspace.utils;

import java.security.MessageDigest;

/**
 * @author : Ice'Clean
 * @date : 2022-06-25
 */

public class Md5Utils {

    private static final String SALT = "IceClean";

    public static String encode(String password) {
        MessageDigest md5;
        password = password + SALT;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        char[] charArray = password.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++){
            byteArray[i] = (byte) charArray[i];
        }

        byte[] md5Bytes = md5.digest(byteArray);

        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }
}
