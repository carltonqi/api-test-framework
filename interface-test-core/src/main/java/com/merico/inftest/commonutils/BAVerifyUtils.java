package com.merico.inftest.commonutils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class BAVerifyUtils {
    private static final String AUTH_METHOD = "MWS";
    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static Map<String, Object> getBasicAuthHeader(String method, String url, String appKey, String appSecret) {
        try {
            Map<String, Object> header = new HashMap<String, Object>();
            String date = String.valueOf(new Date().getTime());
            String encryptStr = String.format("%s %s\n%s", method, new URL(url).getPath(), date);
            String sign = hmacSHA1(appSecret, encryptStr);
            header.put("Date", date);
            header.put("Authorization", String.format("%s %s:%s", AUTH_METHOD, appKey, sign));
            return header;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    public static String hmacSHA1(String key, String data) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes("utf-8"));
            //必须使用 commons-codec 1.5及以上版本，否则base64加密后会出现换行问题
            return Base64.encodeBase64String(rawHmac);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
