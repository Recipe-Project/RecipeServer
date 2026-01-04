package com.recipe.app.src.common.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.recipe.app.src.common.client.apple.dto.ApplePublicKeyResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    private final Cache<String, String> accessTokenBlacklistCache;
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final static String TOKEN_KEY = "userId";
    private final static String ACCESS_TOKEN_BLACKLIST_VALUE = "access_token_blacklist";
    private final static String TOKEN_HEADER = "Authorization";
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-ms}")
    private long accessTokenValidMillisecond;
    @Value("${jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidMillisecond;

    public JwtUtil(@Qualifier("accessTokenBlacklistCache") Cache<String, String> accessTokenBlacklistCache) {
        this.accessTokenBlacklistCache = accessTokenBlacklistCache;
    }

    public String createAccessToken(Long userId) {

        Date now = new Date();
        Key key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), SignatureAlgorithm.HS256.getJcaName());

        return Jwts.builder()
                .claim(TOKEN_KEY, userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {

        Date now = new Date();
        Key key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), SignatureAlgorithm.HS256.getJcaName());

        return Jwts.builder()
                .claim(TOKEN_KEY, userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(key)
                .compact();
    }

    public String resolveAccessToken(HttpServletRequest request) {

        String header = request.getHeader(TOKEN_HEADER);

        return header != null ? header.substring(7) : null;
    }

    public long getUserId(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get(TOKEN_KEY, Long.class);
    }

    public boolean isValidAccessToken(String accessToken) {

        if (accessTokenBlacklistCache.getIfPresent(accessToken) != null) {
            return false;
        }

        return isValidToken(accessToken);
    }

    public boolean isValidRefreshToken(String refreshToken) {

        return isValidToken(refreshToken);
    }

    private boolean isValidToken(String token) {

        try {
            logger.debug(this.secretKey);
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException | SignatureException exception) {
            logger.info("잘못된 Jwt 토큰입니다");
        } catch (ExpiredJwtException exception) {
            logger.info("만료된 Jwt 토큰입니다");
        } catch (UnsupportedJwtException exception) {
            logger.info("지원하지 않는 Jwt 토큰입니다");
        }

        return false;
    }

    public void setAccessTokenBlacklist(String accessToken) {

        accessTokenBlacklistCache.put(accessToken, ACCESS_TOKEN_BLACKLIST_VALUE);
    }

    public Claims parseAppleIdToken(String idToken, ApplePublicKeyResponse publicKey) {

        try {
            PublicKey key = generateApplePublicKey(publicKey);

            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(idToken)
                    .getBody();
        } catch (Exception e) {
            logger.error("Apple id_token 검증 실패", e);
            throw new IllegalArgumentException("Apple id_token 검증에 실패했습니다.", e);
        }
    }

    private PublicKey generateApplePublicKey(ApplePublicKeyResponse publicKey) {

        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(publicKey.getN());
            byte[] eBytes = Base64.getUrlDecoder().decode(publicKey.getE());

            BigInteger n = new BigInteger(1, nBytes);
            BigInteger e = new BigInteger(1, eBytes);

            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(n, e);
            KeyFactory keyFactory = KeyFactory.getInstance(publicKey.getKty());

            return keyFactory.generatePublic(publicKeySpec);
        } catch (Exception exception) {
            logger.error("Apple Public Key 생성 실패", exception);
            throw new IllegalArgumentException("Apple Public Key 생성에 실패했습니다.", exception);
        }
    }
}
