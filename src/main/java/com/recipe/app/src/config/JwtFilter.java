package com.recipe.app.src.config;

import com.recipe.app.src.common.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String accessToken = jwtUtil.resolveAccessToken((HttpServletRequest) request);
        String requestURI = ((HttpServletRequest) request).getRequestURI();

        if (!StringUtils.hasText(accessToken)) {
            logger.info("필수 토큰이 없습니다., uri: {}", requestURI);
        } else if (jwtUtil.isValidAccessToken(accessToken)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(String.valueOf(jwtUtil.getUserId(accessToken)));
            Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.info("Security context에 인증 정보를 저장했습니다, uri: {}", requestURI);
        } else {
            logger.info("유효한 Jwt 토큰이 없습니다, uri: {}", requestURI);
        }

        chain.doFilter(request, response);
    }
}
