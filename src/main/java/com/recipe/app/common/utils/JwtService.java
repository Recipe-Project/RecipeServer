package com.recipe.app.common.utils;

import com.recipe.app.src.user.domain.JwtBlacklist;
import com.recipe.app.src.user.infra.JwtBlacklistRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class JwtService {

    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.token-header}")
    private String tokenHeader;
    @Value("${jwt.token-validity-in-ms}")
    private long tokenValidMillisecond;

    public JwtService(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    public String createJwt(Long userId) {
        Date now = new Date();
        Key key = new SecretKeySpec(Base64.getDecoder().decode(this.secretKey), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(key)
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);

        if (!StringUtils.hasText(token)) {
            token = request.getParameter(tokenHeader);
        }

        return StringUtils.hasText(token) ? token : null;
    }

    public int getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Integer.class);
    }

    @Transactional(readOnly = true)
    public boolean validateToken(String token) {
        try {
            logger.debug(this.secretKey);
            if (jwtBlacklistRepository.findById(token).isPresent())
                return false;
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

        String jwt = resolveToken(request);
        jwtBlacklistRepository.save(new JwtBlacklist(jwt));
    }
}
