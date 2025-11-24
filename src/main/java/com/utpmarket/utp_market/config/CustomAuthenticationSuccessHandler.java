package com.utpmarket.utp_market.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAuthenticationSuccessHandler.class);

    public CustomAuthenticationSuccessHandler() {
        // Establecer una URL por defecto en caso de que ninguna lógica coincida
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        // Usamos el método de la clase padre para una redirección limpia
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        logger.info("User {} successfully authenticated with authorities: {}", authentication.getName(), authorities);

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            logger.info("Redirecting user {} to /admin/dashboard", authentication.getName());
            return "/admin/dashboard";
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VENDEDOR"))) {
            logger.info("Redirecting user {} to /vendedor/dashboard", authentication.getName());
            return "/vendedor/dashboard";
        } else {
            // Para ROLE_ESTUDIANTE y cualquier otro rol, redirigir a la página principal
            logger.info("Redirecting user {} to /", authentication.getName());
            return "/";
        }
    }
}