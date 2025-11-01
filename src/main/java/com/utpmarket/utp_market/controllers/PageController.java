package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.FAQItem; // Importar FAQItem
import com.utpmarket.utp_market.models.entity.user.EstudianteDetalles;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.EstudianteDetallesRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import com.utpmarket.utp_market.models.entity.order.Pedido;
import com.utpmarket.utp_market.services.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class PageController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EstudianteDetallesRepository estudianteDetallesRepository;

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/about-us")
    public String aboutUs() {
        return "pages/about";
    }

    @GetMapping("/help")
    public String help(Model model) { // Add Model parameter
        List<FAQItem> faqItems = List.of(
                new FAQItem("¿Cómo puedo contactar al soporte?",
                        "Puedes escribirnos directamente desde el chat de WhatsApp disponible en la plataforma. Solo haz clic en el botón de ayuda y uno de nuestros asesores te responderá."),
                new FAQItem("¿En qué horario atiende el centro de ayuda?",
                        "Nuestro equipo responde tus dudas de lunes a sábado de 9:00 a.m. a 9:00 p.m. Fuera de ese horario puedes dejar tu mensaje y lo atenderemos en cuanto estemos disponibles."),
                new FAQItem("¿Qué tipo de dudas puedo resolver en el chat?",
                        "Puedes consultar sobre compras, ventas, uso de la plataforma, problemas con tu perfil o cualquier duda general sobre UTP Market."),
                new FAQItem("¿Hay algún costo por usar el centro de ayuda?",
                        "No. El servicio de soporte y el chat de WhatsApp son totalmente gratuitos para toda la comunidad UTP."),
                new FAQItem("¿Qué hago si no responden mi consulta?",
                        "En caso de que no recibas respuesta en el chat, puedes enviar un reporte desde tu perfil en la sección “Soporte” y un asesor se pondrá en contacto contigo.")
        );
        model.addAttribute("faqItems", faqItems);
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
    public String perfil(HttpSession session, Model model, RedirectAttributes redirectAttributes) { // Added RedirectAttributes
        Usuario usuario;
        try {
            usuario = getAuthenticatedUser(session, redirectAttributes);
            session.setAttribute("usuario", usuario);
        } catch (IllegalStateException e) {
            return "redirect:/auth/login";
        }

        List<Pedido> pedidos = pedidoService.obtenerHistorialPedidosPorUsuario(usuario.getId());
        model.addAttribute("pedidos", pedidos);


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
        Usuario usuarioExistente;
        try {
            usuarioExistente = getAuthenticatedUser(session, redirectAttributes);
        } catch (IllegalStateException e) {
            return "redirect:/auth/login";
        }

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
        Usuario usuarioExistente;
        try {
            usuarioExistente = getAuthenticatedUser(session, redirectAttributes);
        } catch (IllegalStateException e) {
            return "redirect:/auth/login";
        }

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

    private Usuario getAuthenticatedUser(HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioEnSesion = (Usuario) session.getAttribute("usuario");
        if (usuarioEnSesion == null) {
            throw new IllegalStateException("Usuario no autenticado.");
        }

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(usuarioEnSesion.getId());
        if (optionalUsuario.isEmpty()) {
            session.invalidate();
            throw new IllegalStateException("Usuario no encontrado en la base de datos.");
        }
        return optionalUsuario.get();
    }
}