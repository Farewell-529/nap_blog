package com.nap_blog.utils;


import java.nio.charset.StandardCharsets;
import java.security.*;

public class AvatarUtil {
    public static String getGravatarUrl(String email) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(email.trim().toLowerCase().getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}