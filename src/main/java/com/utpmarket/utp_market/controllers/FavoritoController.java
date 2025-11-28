package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/toggle/{productId}")
    public ResponseEntity<?> toggleFavorito(@PathVariable Long productId, Principal principal) {
        Long userId;
        try {
            userId = getUserIdFromPrincipal(principal);
        } catch (IllegalStateException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Debes iniciar sesión para realizar esta acción.");
            return ResponseEntity.status(401).body(errorResponse); // 401 Unauthorized
        }

        System.out.println("FavoritoController: toggleFavorito called.");
        System.out.println("FavoritoController: productId = " + productId + ", userId = " + userId);

        try {
            boolean added = favoritoService.toggleFavorito(userId, productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("added", added);
            response.put("message", added ? "Producto añadido a favoritos" : "Producto eliminado de favoritos");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            System.err.println("FavoritoController Error: " + e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            System.err.println("FavoritoController Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al procesar favoritos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    @GetMapping("/isFavorito/{productId}")
    public ResponseEntity<?> isFavorito(@PathVariable Long productId, Principal principal) {
        Long userId;
        try {
            userId = getUserIdFromPrincipal(principal);
        } catch (IllegalStateException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorito", false);
            return ResponseEntity.ok(response);
        }

        System.out.println("FavoritoController: isFavorito called.");
        System.out.println("FavoritoController: productId = " + productId + ", userId = " + userId);

        try {
            boolean isFav = favoritoService.isFavorito(userId, productId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("isFavorito", isFav);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("FavoritoController Unexpected Error: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error al verificar favoritos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Obtiene el ID del usuario desde el Principal (compatible con JWT)
     * El Principal contiene el email del usuario extraído del token JWT
     */
    private Long getUserIdFromPrincipal(Principal principal) {
        if (principal == null) {
            throw new IllegalStateException("Usuario no autenticado.");
        }

        // El email viene del token JWT (subject del token)
        String email = principal.getName();

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado con email: " + email));

        return usuario.getId();
    }
}