package com.recipe.app.common.utils;

import com.google.api.client.util.Value;
import com.recipe.app.config.secret.Secret;
import com.recipe.app.src.user.mapper.JwtBlacklistRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token-header}")
    private String tokenHeader;

    @Value("${jwt.token-validity-in-ms}")
    private String tokenValidMillisecond;

    private final UserDetailsService userDetailsService;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final Logger logger = LoggerFactory.getLogger(JwtService.class);

    public String createJwt(int userId) {
        Date now = new Date();
        return Jwts.builder()
                .claim("userId", userId)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidMillisecond))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_SECRET_KEY)
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
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Integer.class);
    }

    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(getUserId(token)));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public boolean validateToken(String token) {
        try {
            logger.debug(this.secretKey);
            if(jwtBlacklistRepository.findById(token).isPresent())
                return false;
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(this.secretKey).build().parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (SecurityException | MalformedJwtException | IllegalArgumentException exception) {
            logger.info("잘못된 Jwt 토큰입니다");
        } catch (ExpiredJwtException exception) {
            logger.info("만료된 Jwt 토큰입니다");
        } catch (UnsupportedJwtException exception) {
            logger.info("지원하지 않는 Jwt 토큰입니다");
        }

        return false;
    }

    public String getTokenHeader() {
        return this.tokenHeader;
    }
}
