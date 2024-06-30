package com.recipe.app.common.utils;

import com.recipe.app.src.user.domain.JwtBlacklist;
import com.recipe.app.src.user.infra.JwtBlacklistRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.access-token-validity-in-ms}")
    private long accessTokenValidMillisecond;
    @Value("${jwt.refresh-token-validity-in-ms}")
    private long refreshTokenValidMillisecond;

    public JwtUtil(RedisTemplate<String, String> redisTemplate, JwtBlacklistRepository jwtBlacklistRepository) {
        this.redisTemplate = redisTemplate;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public String createAccessToken(Long userId) {

        Date now = new Date();
        Key key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), SignatureAlgorithm.HS256.getJcaName());

        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + accessTokenValidMillisecond))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken(Long userId) {

        Date now = new Date();
        Key key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), SignatureAlgorithm.HS256.getJcaName());

        String token = Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + refreshTokenValidMillisecond))
                .signWith(key)
                .compact();

        redisTemplate.opsForValue().set("refresh_token_user_id_" + userId, token, Duration.ofMillis(refreshTokenValidMillisecond));

        return token;
    }

    public String resolveAccessToken(HttpServletRequest request) {

        String header = request.getHeader("Authorization");

        return header != null ? header.substring(7) : null;
    }

    public long getUserId(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    @Transactional(readOnly = true)
    public boolean isValidAccessToken(String accessToken) {

        if (jwtBlacklistRepository.findById(accessToken).isPresent()) {
            return false;
        }

        return isValidToken(accessToken);
    }

    public boolean isValidRefreshToken(String refreshToken) {

        if (isValidToken(refreshToken)) {

            long userId = getUserId(refreshToken);
            String foundRefreshToken = redisTemplate.opsForValue().get("refresh_token_user_id_" + userId);

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
    public void createJwtBlacklist(HttpServletRequest request) {

        String jwt = resolveAccessToken(request);
        jwtBlacklistRepository.save(new JwtBlacklist(jwt));
    }
}
