package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.AuthenticationResponse;
import com.utpmarket.utp_market.models.dto.SolicitudLogin;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.enums.RegistroResultado;
import com.utpmarket.utp_market.services.AuthService;
import com.utpmarket.utp_market.services.MyUserDetailsService;
import com.utpmarket.utp_market.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody SolicitudLogin solicitudLogin) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(solicitudLogin.email(), solicitudLogin.password())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(solicitudLogin.email());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        RegistroResultado resultado = authService.registrarUsuario(usuario);

        if (resultado == RegistroResultado.EXITO) {
            return ResponseEntity.ok("Usuario registrado correctamente.");
        } else {
            String mensajeError = switch (resultado) {
                case CORREO_INVALIDO -> "El correo debe ser institucional (@utp.edu.pe).";
                case CORREO_YA_REGISTRADO -> "El correo ya está registrado.";
                case ERROR_ROL_NO_ENCONTRADO -> "Error interno: Rol de usuario no encontrado.";
                default -> "Ocurrió un error durante el registro.";
            };
            return ResponseEntity.badRequest().body(mensajeError);
        }
    }
}