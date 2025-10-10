package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String registrarUsuario(Usuario usuario){
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            return "El correo ya está registrado.";
        }

        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol("estudiante");
        usuarioRepository.save(usuario);
        return "Usuario registrado correctamente.";
    }

    public Optional<Usuario> login(String email, String password) {
        Optional<Usuario> usuarioLogueado = usuarioRepository.findByEmail(email);

        if (usuarioLogueado.isPresent()) {
            Usuario usuario = usuarioLogueado.get();
            if (passwordEncoder.matches(password, usuario.getPassword())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

}