package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", email);

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("User not found with email: {}", email);
                    return new UsernameNotFoundException("Usuario no encontrado con el email: " + email);
                });

        if (usuario.getActivo() == null || !usuario.getActivo()) {
            logger.warn("User {} is inactive", email);
            throw new UsernameNotFoundException("Usuario inactivo: " + email);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (usuario.getRol() != null && usuario.getRol().getNombre() != null) {
            String roleName = "ROLE_" + usuario.getRol().getNombre().toUpperCase();
            authorities.add(new SimpleGrantedAuthority(roleName));
            logger.info("User {} loaded successfully with role: {}", email, roleName);
        } else {
            logger.warn("User {} has no role assigned", email);
        }

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }
}
