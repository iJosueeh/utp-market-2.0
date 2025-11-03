package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.services.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.utpmarket.utp_market.models.enums.RegistroResultado;
import java.util.Optional;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model
    ) {
        Optional<Usuario> usuario = authService.login(email, password);

        if (usuario.isPresent()) {
            session.setAttribute("usuario", usuario.get());
            return "redirect:/";
        } else {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "auth/login";
        }
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

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/auth/login";
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String currentPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return "redirect:/auth/login";
        }

        try {
            authService.changePassword(usuario, currentPassword, newPassword, confirmPassword);
            session.setAttribute("success", "Contraseña actualizada correctamente.");
        } catch (Exception e) {
            session.setAttribute("error", e.getMessage());
        }

        return "redirect:/perfil";
    }

}