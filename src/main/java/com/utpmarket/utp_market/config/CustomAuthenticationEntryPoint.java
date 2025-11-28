package com.utpmarket.utp_market.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {

        String requestUri = request.getRequestURI();

        // Si la petición es a la API, devolver 401 Unauthorized
        if (requestUri.startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error: No autorizado");
        } else {

            response.sendRedirect("/auth/login?error=expired");
        }
    }
}