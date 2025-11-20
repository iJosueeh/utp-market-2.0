package com.utpmarket.utp_market.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        logger.info("User {} successfully authenticated with authorities: {}", authentication.getName(), authorities);

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            logger.info("Redirecting user {} to /admin/dashboard", authentication.getName());
            response.sendRedirect("/admin/dashboard");
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_PROFESSIONAL"))) {
            logger.info("Redirecting user {} to /profesional/dashboard", authentication.getName());
            response.sendRedirect("/profesional/dashboard");
        } else {
            logger.info("Redirecting user {} to /", authentication.getName());
            response.sendRedirect("/");
        }
    }
}