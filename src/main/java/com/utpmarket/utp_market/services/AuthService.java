package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.user.EstudianteDetalles;
import com.utpmarket.utp_market.models.entity.user.Rol;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.EstudianteDetallesRepository;
import com.utpmarket.utp_market.repository.RolRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.utpmarket.utp_market.models.enums.RegistroResultado;
import java.util.Collections;
import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private EstudianteDetallesRepository estudianteDetallesRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> {
                    return new UsernameNotFoundException("No se encontró un usuario con el correo: " + email);
                });

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().toUpperCase()));

        return new User(usuario.getEmail(), usuario.getPassword(), authorities);
    }

    public void changePassword(Usuario usuario, String currentPassword, String newPassword, String confirmPassword) throws Exception {
        if (!passwordEncoder.matches(currentPassword, usuario.getPassword())) {
            throw new Exception("La contraseña actual es incorrecta");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("Las contraseñas no coinciden");
        }
        usuario.setPassword(passwordEncoder.encode(newPassword));
        usuarioRepository.save(usuario);
    }
}