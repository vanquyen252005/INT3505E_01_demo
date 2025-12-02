package com.example.webhook.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class SignatureUtil {
    public static String computeSignature(String secret, String payload, String timestamp, String eventId) {
        try {
            String data = payload + "|" + timestamp + "|" + eventId;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : raw) {
                sb.append(String.format("%02x", b));
            }
            return "sha256=" + sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // constant-time equals to avoid timing attacks
    public static boolean secureEquals(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int res = 0;
        for (int i = 0; i < a.length(); i++) {
            res |= a.charAt(i) ^ b.charAt(i);
        }
        return res == 0;
    }
}
