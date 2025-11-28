package com.utpmarket.utp_market.models.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuesta de autenticación exitosa
 * Contiene el token JWT y la información del usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String refreshToken;
    private String type = "Bearer";
    private String email;
    private String nombre;
    private String rol;
    private Long expiresIn;

    public AuthResponse(String token, String refreshToken, String email, String nombre, String rol, Long expiresIn) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
        this.expiresIn = expiresIn;
        this.type = "Bearer";
    }
}
