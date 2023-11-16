package com.lnp.project.common;

import java.time.Instant;

public class JWTKey {

    private static int getRandom() {
        int random = (int) Math.round(Math.random() * 1000000000);
        return random;
    }

    private static long getTimestamp() {
        long timestamp = Instant.now().getEpochSecond();
        return timestamp;
    }

    public static String getToken() {
        int random = getRandom();
        long timestamp = getTimestamp();
        String secretKey = io.jsonwebtoken.impl.TextCodec.BASE64.encode("UFMwMDE1NjU5ZGQ1NTZmMWJlNzg2ZTUyYTZlMTEzMzkwYTM2MmNlYg==");
//        String secretKey = io.jsonwebtoken.impl.TextCodec.BASE64.encode("UFMwMDQ2OTNkYmM0YzlkMGZkODNlOWQyMzhkZWEzNDJjZGQwMzBkMTE2OTgzODAyOTM=");
        String token = io.jsonwebtoken.Jwts.builder()
                .setIssuer("PSPRINT")
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("alg", "HS256")
                .claim("timestamp", timestamp)
                .claim("partnerId", "PS001565") // PARTNER ID
//                .claim("partnerId", "PS004693") // PARTNER ID
                .claim("reqid", random) // Random request no
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, secretKey)
                .compact();

        return token;
    }
}
