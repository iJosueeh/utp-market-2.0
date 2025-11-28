package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.ProductoDTO;
import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.models.entity.product.Reviews;
import com.utpmarket.utp_market.models.entity.user.EstudianteDetalles;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.EstudianteDetallesRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.services.FavoritoService;
import com.utpmarket.utp_market.services.PedidoService;
import com.utpmarket.utp_market.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/perfil")
@PreAuthorize("isAuthenticated()")
public class PerfilController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteDetallesRepository estudianteDetallesRepository;

    @Autowired
    private FavoritoService favoritoService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private ReviewService reviewService;

    private static final List<String> CARRERAS_UTP = Arrays.asList(
            "Ingeniería de Sistemas e Informática",
            "Ingeniería de Software",
            "Ingeniería Industrial",
            "Ingeniería Civil",
            "Administración de Empresas",
            "Contabilidad",
            "Derecho",
            "Arquitectura");

    @GetMapping
    public String verPerfil(Principal principal, Model model) {
        Usuario usuario = usuarioRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado en la base de datos"));

        // Obtener pedidos del usuario
        List<Pedido> pedidos = pedidoService.obtenerHistorialPedidosPorUsuario(usuario.getId());
        model.addAttribute("pedidos", pedidos);

        // Datos del usuario
        model.addAttribute("user", usuario);

        // Reseñas del usuario
        List<Reviews> reseñasUsuario = reviewService.obtenerReviewsPorUsuario(usuario.getId());
        model.addAttribute("reviews", reseñasUsuario);

        // Carreras disponibles
        model.addAttribute("carrerasUtp", CARRERAS_UTP);

        // Favoritos del usuario
        List<ProductoDTO> favoritos = favoritoService.getFavoritosByUsuarioDTO(usuario.getId());
        model.addAttribute("favoritos", favoritos);

        return "pages/perfil";
    }

    /**
     * Actualiza la información personal del usuario.
     * Incluye: nombre, apellido, teléfono y fecha de nacimiento.
     */
    @PostMapping("/actualizar")
    public String actualizarInformacionPersonal(
            @ModelAttribute Usuario usuarioActualizado,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuarioExistente = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

            // Actualizar datos básicos
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setApellido(usuarioActualizado.getApellido());

            // Actualizar detalles de estudiante si existen
            if (usuarioActualizado.getEstudianteDetalles() != null) {
                EstudianteDetalles estudianteDetallesExistente = getOrCreateEstudianteDetalles(usuarioExistente);
                estudianteDetallesExistente.setTelefono(usuarioActualizado.getEstudianteDetalles().getTelefono());
                estudianteDetallesExistente
                        .setFecha_nacimiento(usuarioActualizado.getEstudianteDetalles().getFecha_nacimiento());
                estudianteDetallesRepository.save(estudianteDetallesExistente);
                usuarioExistente.setEstudianteDetalles(estudianteDetallesExistente);
            }

            usuarioRepository.save(usuarioExistente);
            redirectAttributes.addFlashAttribute("success", "Información personal actualizada correctamente");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar información: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * Actualiza la información académica UTP del usuario.
     * Incluye: código de estudiante, ciclo y carrera.
     */
    @PostMapping("/actualizar-utp")
    public String actualizarInformacionUtp(
            @ModelAttribute Usuario usuarioActualizado,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        try {
            Usuario usuarioExistente = usuarioRepository.findByEmail(principal.getName())
                    .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

            EstudianteDetalles estudianteDetallesExistente = getOrCreateEstudianteDetalles(usuarioExistente);

            // Actualizar información académica
            estudianteDetallesExistente
                    .setCodigo_estudiante(usuarioActualizado.getEstudianteDetalles().getCodigo_estudiante());
            estudianteDetallesExistente.setCiclo(usuarioActualizado.getEstudianteDetalles().getCiclo());
            estudianteDetallesExistente.setCarrera(usuarioActualizado.getEstudianteDetalles().getCarrera());
            estudianteDetallesRepository.save(estudianteDetallesExistente);

            redirectAttributes.addFlashAttribute("success", "Información UTP actualizada correctamente");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar información UTP: " + e.getMessage());
            return "redirect:/perfil";
        }
    }

    /**
     * Método helper para obtener o crear detalles de estudiante.
     */
    private EstudianteDetalles getOrCreateEstudianteDetalles(Usuario usuario) {
        EstudianteDetalles estudianteDetalles = usuario.getEstudianteDetalles();
        if (estudianteDetalles == null) {
            estudianteDetalles = new EstudianteDetalles();
            estudianteDetalles.setUsuario(usuario);
        }
        return estudianteDetalles;
    }
}
