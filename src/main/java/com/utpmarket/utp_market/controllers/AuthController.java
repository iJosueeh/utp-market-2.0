package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.enums.RegistroResultado;
import com.utpmarket.utp_market.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint para registrar nuevos usuarios.
     * Valida que el correo sea institucional (@utp.edu.pe) y que no esté ya
     * registrado.
     */
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