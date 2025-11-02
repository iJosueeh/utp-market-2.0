package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.user.EstudianteDetalles;
import com.utpmarket.utp_market.models.entity.user.Rol;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.EstudianteDetallesRepository;
import com.utpmarket.utp_market.repository.RolRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.utpmarket.utp_market.models.enums.RegistroResultado;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstudianteDetallesRepository estudianteDetallesRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        public RegistroResultado registrarUsuario(Usuario usuario){
            if (!usuario.getEmail().toLowerCase().endsWith("@utp.edu.pe")) {
                return RegistroResultado.CORREO_INVALIDO;
            }
            if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
                return RegistroResultado.CORREO_YA_REGISTRADO;
            }
    
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            try {
                Rol userRole = rolRepository.findByNombre("estudiante").orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
                usuario.setRol(userRole);
                usuarioRepository.save(usuario);
    
                EstudianteDetalles estudianteDetalles = new EstudianteDetalles();
                estudianteDetalles.setUsuario(usuario);
    
                String emailPrefix = usuario.getEmail().split("@")[0];
                estudianteDetalles.setCodigo_estudiante(emailPrefix.toUpperCase());
    
                estudianteDetalles.setCiclo("1");
                estudianteDetalles.setCarrera("PENDIENTE");
    
                estudianteDetallesRepository.save(estudianteDetalles);
    
                return RegistroResultado.EXITO;
            } catch (RuntimeException e) {
                if (e.getMessage().contains("Rol no encontrado")) {
                    return RegistroResultado.ERROR_ROL_NO_ENCONTRADO;
                }
                return RegistroResultado.ERROR_DESCONOCIDO;
            }
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