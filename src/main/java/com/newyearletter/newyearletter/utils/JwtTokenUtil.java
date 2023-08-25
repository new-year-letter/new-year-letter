package com.newyearletter.newyearletter.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtil {
    private static final long ACCESS_EXPIRE_TIME = 1000 * 60 * 30;
    private static final long REFRESH_EXPIRE_TIME = 1000 * 60 * 60 * 7;

    private static Claims extractClaims(String token, String key) {
        return Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
    }

    //claim에 담은 userSeq 받아오기
    public static Long getUserSeq(String token, String key) {
        // createToken 메서드에서 put한 "userName"을 불러온다.
        return extractClaims(token, key).get("userSeq", Long.class);
    }

    //claim에 담은 userName 받아오기
    public static String getUserName(String token, String key) {
        // createToken 메서드에서 put한 "userName"을 불러온다.
        return extractClaims(token, key).get("userName", String.class);
    }

    // 현재 시간보다 만료된 token인지 확인
    public static boolean isExpired(String token, String secretkey) {
        Date expiredDate = extractClaims(token, secretkey).getExpiration();
        return expiredDate.before(new Date());
    }

    public static void updateExpired(String token, String secretkey){

    }

    //access token
    public static String createAccessToken(String userName, Long userSeq, String key) {
        Claims claims = Jwts.claims();
        // Map 형식으로 되어 있음
        claims.put("userSeq", userSeq);
        claims.put("userName", userName);
        //만료기한 30분


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    //refresh token
    public static String createRefreshToken(String key) {
        Claims claims = Jwts.claims();
        // 만료기한 7일
        long expireTimeMs = 1000 * 60 * 60 * 7;

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }
}
