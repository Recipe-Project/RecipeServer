package com.recipe.app.common.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private final static String TOKEN_KEY = "userId";
    private final static String REFRESH_TOKEN_KEY_PREFIX = "refresh_token_user_id_";
    private final static String ACCESS_TOKEN_BLACKLIST_VALUE = "access_token_blacklist";
    private final static String TOKEN_HEADER = "Authorization";
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-ms}")
    private long accessTokenValidMillisecond;
    @Value("${jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidMillisecond;

    public JwtUtil(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
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

        String token = Jwts.builder()
                .claim(TOKEN_KEY, userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(key)
                .compact();

        redisTemplate.opsForValue().set(REFRESH_TOKEN_KEY_PREFIX + userId, token, Duration.ofMillis(refreshTokenValidMillisecond));

        return token;
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

    @Transactional(readOnly = true)
    public boolean isValidAccessToken(String accessToken) {

        if (StringUtils.hasText(redisTemplate.opsForValue().get(accessToken))) {
            return false;
        }

        return isValidToken(accessToken);
    }

    public boolean isValidRefreshToken(String refreshToken) {

        if (isValidToken(refreshToken)) {

            long userId = getUserId(refreshToken);
            String foundRefreshToken = redisTemplate.opsForValue().get(REFRESH_TOKEN_KEY_PREFIX + userId);

            return refreshToken.equals(foundRefreshToken);
        }

        return false;
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

    @Transactional
    public void removeRefreshToken(Long userId) {

        redisTemplate.delete(REFRESH_TOKEN_KEY_PREFIX + userId);
    }

    @Transactional
    public void setAccessTokenBlacklist(String accessToken) {

        redisTemplate.opsForValue().set(accessToken, ACCESS_TOKEN_BLACKLIST_VALUE, Duration.ofMillis(accessTokenValidMillisecond));
    }
}
