package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.utpmarket.utp_market.models.enums.RegistroResultado;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {

        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
        }

        if (logout != null) {
            model.addAttribute("mensaje", "Has cerrado sesión exitosamente.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registrarUsuario(@ModelAttribute("usuario") Usuario usuario, Model model) {
        RegistroResultado resultado = authService.registrarUsuario(usuario);

        if (resultado == RegistroResultado.EXITO) {
            model.addAttribute("success", "Usuario registrado correctamente.");
            return "auth/login";
        } else {
            String mensajeError = switch (resultado) {
                case CORREO_INVALIDO -> "El correo debe ser institucional (@utp.edu.pe).";
                case CORREO_YA_REGISTRADO -> "El correo ya está registrado.";
                case ERROR_ROL_NO_ENCONTRADO -> "Error interno: Rol de usuario no encontrado.";
                default -> "Ocurrió un error durante el registro.";
            };
            model.addAttribute("error", mensajeError);
            return "auth/register";
        }
    }

    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 Principal principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/auth/login";
        }

        String email = principal.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        try {
            authService.changePassword(usuario, currentPassword, newPassword, confirmPassword);
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }
}