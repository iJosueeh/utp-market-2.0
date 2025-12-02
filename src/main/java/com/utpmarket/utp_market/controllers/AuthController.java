package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.exception.InvalidTokenException;
import com.utpmarket.utp_market.models.dto.auth.AuthResponse;
import com.utpmarket.utp_market.models.dto.auth.LoginRequest;
import com.utpmarket.utp_market.models.dto.auth.MessageResponse;
import com.utpmarket.utp_market.models.dto.auth.RefreshTokenRequest;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.enums.RegistroResultado;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.AuthService;
import com.utpmarket.utp_market.services.MyUserDetailsService;
import com.utpmarket.utp_market.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Endpoint para autenticar usuarios y generar token JWT.
     * 
     * @param loginRequest Credenciales del usuario (email y password)
     * @return Token JWT y datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        try {
            logger.info("Attempting login for user: {}", loginRequest.getEmail());

            // Autenticar credenciales
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()));

            // Cargar detalles del usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());

            // Generar tokens
            String accessToken = jwtUtil.generateToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Obtener información adicional del usuario
            Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Extraer rol (sin el prefijo ROLE_)
            String rol = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .map(authority -> authority.replace("ROLE_", ""))
                    .orElse("ESTUDIANTE");

            // Crear Cookie HttpOnly para el accessToken
            ResponseCookie cookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(false) // Cambiar a true en producción con HTTPS
                    .path("/")
                    .maxAge(jwtUtil.getExpiration() / 1000) // Convertir ms a segundos
                    .sameSite("Strict")
                    .build();

            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

            // Crear respuesta
            AuthResponse authResponse = new AuthResponse(
                    accessToken,
                    refreshToken,
                    usuario.getEmail(),
                    usuario.getNombreCompleto(),
                    rol,
                    jwtUtil.getExpiration());

            logger.info("User {} logged in successfully with role {}", loginRequest.getEmail(), rol);
            return ResponseEntity.ok(authResponse);

        } catch (BadCredentialsException e) {
            logger.warn("Failed login attempt for user: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Credenciales inválidas"));
        } catch (Exception e) {
            logger.error("Error during login for user {}: {}", loginRequest.getEmail(), e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error durante el login: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public RedirectView logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return new RedirectView("/auth/login");
    }

    /**
     * Endpoint para renovar el access token usando un refresh token válido.
     * 
     * @param request Refresh token
     * @return Nuevo access token y refresh token
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            // Validar refresh token
            if (!jwtUtil.validateToken(refreshToken)) {
                throw new InvalidTokenException("Refresh token inválido o expirado");
            }

            // Extraer email del refresh token
            String email = jwtUtil.extractEmail(refreshToken);

            // Cargar usuario
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            Usuario usuario = usuarioRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Generar nuevos tokens
            String newAccessToken = jwtUtil.generateToken(userDetails);
            String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Extraer rol
            String rol = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst()
                    .map(authority -> authority.replace("ROLE_", ""))
                    .orElse("ESTUDIANTE");

            // Crear respuesta
            AuthResponse response = new AuthResponse(
                    newAccessToken,
                    newRefreshToken,
                    usuario.getEmail(),
                    usuario.getNombreCompleto(),
                    rol,
                    jwtUtil.getExpiration());

            logger.info("Token refreshed successfully for user: {}", email);
            return ResponseEntity.ok(response);

        } catch (InvalidTokenException e) {
            logger.warn("Invalid refresh token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error refreshing token: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error al renovar el token"));
        }
    }

    /**
     * Endpoint para registrar nuevos usuarios.
     * Valida que el correo sea institucional (@utp.edu.pe) y que no esté ya
     * registrado.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario usuario) {
        RegistroResultado resultado = authService.registrarUsuario(usuario);

        if (resultado == RegistroResultado.EXITO) {
            return ResponseEntity.ok(new MessageResponse("Usuario registrado correctamente."));
        } else {
            String mensajeError = switch (resultado) {
                case CORREO_INVALIDO -> "El correo debe ser institucional (@utp.edu.pe).";
                case CORREO_YA_REGISTRADO -> "El correo ya está registrado.";
                case ERROR_ROL_NO_ENCONTRADO -> "Error interno: Rol de usuario no encontrado.";
                default -> "Ocurrió un error durante el registro.";
            };
            return ResponseEntity.badRequest().body(new MessageResponse(mensajeError));
        }
    }

    /**
     * Endpoint para cambiar la contraseña del usuario.
     */
    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword,
            java.security.Principal principal,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        try {
            Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            authService.changePassword(usuario, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente");
            return "redirect:/perfil";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
            return "redirect:/perfil";
        }
    }
}