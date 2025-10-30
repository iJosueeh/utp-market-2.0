package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.entity.user.EstudianteDetalles;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.EstudianteDetallesRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays; // Import Arrays
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteDetallesRepository estudianteDetallesRepository;

    @GetMapping("/about-us")
    public String aboutUs() {
        return "pages/about";
    }

    @GetMapping("/help")
    public String help() {
        return "pages/help";
    }

    @GetMapping("/sedes")
    public String sedes(Model model) {

        List<Map<String, String>> sedes = List.of(
                Map.of(
                        "nombre", "Sede Norte",
                        "direccion", "Panamericana Norte, Av. Alfredo Mendiola 6377, Los Olivos 15306",
                        "telefono", "970804148",
                        "mapa", "https://www.google.com/maps/place/Panamericana+Norte+6377,+Los+Olivos+15306",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/campus-los-olivos-utp.webp"
                ),
                Map.of(
                        "nombre", "Sede Lima Centro",
                        "direccion", "Avenida Petit Thouars 116, Lima 15046",
                        "telefono", "(01) 3159600",
                        "mapa", "https://www.google.com/maps/place/Avenida+Petit+Thouars+116,+Lima+15046",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/utp-lima-centro.webp"
                ),
                Map.of(
                        "nombre", "Sede Lima Sur",
                        "direccion", "Ctra. Panamericana Sur km 16, Villa El Salvador 15842",
                        "telefono", "(01) 3159600",
                        "mapa", "https://www.google.com/maps/place/Panamericana+Sur+Km+16,+Villa+El+Salvador+15842",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/LimaSur-.webp"
                ),
                Map.of(
                        "nombre", "Sede San Juan de Lurigancho",
                        "direccion", "Av. Wiesse 571, San Juan de Lurigancho 15434",
                        "telefono", "(01) 3159600",
                        "mapa", "https://maps.app.goo.gl/REhYfwjyYBLGhErc6",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/SJL-.webp"
                ),
                Map.of(
                        "nombre", "Sede Ate",
                        "direccion", "Av. Carretera Central 123, Ate, Lima",
                        "telefono", "(01) 3159600",
                        "mapa", "https://maps.app.goo.gl/m6p3aiWzAuyf8mLu7",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/utp-sede-ate.webp"
                )
        );

        model.addAttribute("sedes", sedes);

        return "pages/sedes";
    }

    @GetMapping("/ventas")
    public String ventas(){
        return "pages/shop";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/auth/login";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuario.getId());
        if (optionalUsuario.isEmpty()) {
            session.invalidate();
            return "redirect:/auth/login";
        }
        usuario = optionalUsuario.get();
        session.setAttribute("usuario", usuario);

        model.addAttribute("user", usuario);

        List<String> carrerasUtp = Arrays.asList(
                "Ingeniería de Sistemas e Informática",
                "Ingeniería de Software",
                "Ingeniería Industrial",
                "Ingeniería Civil",
                "Administración de Empresas",
                "Contabilidad",
                "Derecho",
                "Arquitectura"
        );
        model.addAttribute("carrerasUtp", carrerasUtp);

        return "pages/perfil";
    }

    @PostMapping("/usuario/actualizar")
    public String actualizarInformacionPersonal(@ModelAttribute Usuario usuarioActualizado, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioEnSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioEnSesion == null) {
            return "redirect:/auth/login";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioEnSesion.getId());
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/perfil";
        }

        Usuario usuarioExistente = optionalUsuario.get();

        usuarioExistente.setNombre(usuarioActualizado.getNombre());
        usuarioExistente.setApellido(usuarioActualizado.getApellido());

        if (usuarioActualizado.getEstudianteDetalles() != null) {
            EstudianteDetalles estudianteDetallesExistente = usuarioExistente.getEstudianteDetalles();
            if (estudianteDetallesExistente == null) {
                estudianteDetallesExistente = new EstudianteDetalles();
                estudianteDetallesExistente.setUsuario(usuarioExistente);
            }
            estudianteDetallesExistente.setTelefono(usuarioActualizado.getEstudianteDetalles().getTelefono());
            estudianteDetallesExistente.setFecha_nacimiento(usuarioActualizado.getEstudianteDetalles().getFecha_nacimiento());
            estudianteDetallesRepository.save(estudianteDetallesExistente);
            usuarioExistente.setEstudianteDetalles(estudianteDetallesExistente);
        }

        usuarioRepository.save(usuarioExistente);

        session.setAttribute("usuario", usuarioExistente);

        redirectAttributes.addFlashAttribute("success", "Información personal actualizada correctamente.");
        return "redirect:/perfil";
    }

    @PostMapping("/usuario/actualizar-utp")
    public String actualizarInformacionUtp(@ModelAttribute Usuario usuarioActualizado, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioEnSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioEnSesion == null) {
            return "redirect:/auth/login";
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioEnSesion.getId());
        if (optionalUsuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Usuario no encontrado.");
            return "redirect:/perfil";
        }

        Usuario usuarioExistente = optionalUsuario.get();

        EstudianteDetalles estudianteDetallesExistente = usuarioExistente.getEstudianteDetalles();
        if (estudianteDetallesExistente == null) {
            estudianteDetallesExistente = new EstudianteDetalles();
            estudianteDetallesExistente.setUsuario(usuarioExistente);
        }

        estudianteDetallesExistente.setCodigo_estudiante(usuarioActualizado.getEstudianteDetalles().getCodigo_estudiante());
        estudianteDetallesExistente.setCiclo(usuarioActualizado.getEstudianteDetalles().getCiclo());
        estudianteDetallesExistente.setCarrera(usuarioActualizado.getEstudianteDetalles().getCarrera());
        estudianteDetallesRepository.save(estudianteDetallesExistente);

        usuarioExistente.setEstudianteDetalles(estudianteDetallesExistente);
        session.setAttribute("usuario", usuarioExistente);

        redirectAttributes.addFlashAttribute("success", "Información UTP actualizada correctamente.");
        return "redirect:/perfil";
    }

}