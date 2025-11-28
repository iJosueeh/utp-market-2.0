package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.models.dto.FAQItem;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * Controller para páginas generales del sitio.
 * Las funcionalidades de perfil se movieron a PerfilController.
 */
@Controller
public class PageController {

    @GetMapping("/about-us")
    public String aboutUs() {
        return "pages/about";
    }

    @GetMapping("/help")
    public String help(Model model) {
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
                        "En caso de que no recibas respuesta en el chat, puedes enviar un reporte desde tu perfil en la sección \"Soporte\" y un asesor se pondrá en contacto contigo."));
        model.addAttribute("faqItems", faqItems);
        return "pages/help";
    }

    @GetMapping("/sedes")
    public String sedes(Model model) {

        List<Map<String, String>> sedes = List.of(
                Map.of(
                        "nombre", "Sede Norte",
                        "direccion",
                        "Panamericana Norte, Av. Alfredo Mendiola 6377, Los Olivos 15306",
                        "telefono", "970804148",
                        "mapa",
                        "https://www.google.com/maps/place/Panamericana+Norte+6377,+Los+Olivos+15306",
                        "imagen",
                        "https://utp.edu.pe/sites/default/files/campus/campus-los-olivos-utp.webp"),
                Map.of(
                        "nombre", "Sede Lima Centro",
                        "direccion", "Avenida Petit Thouars 116, Lima 15046",
                        "telefono", "(01) 3159600",
                        "mapa",
                        "https://www.google.com/maps/place/Avenida+Petit+Thouars+116,+Lima+15046",
                        "imagen",
                        "https://utp.edu.pe/sites/default/files/campus/utp-lima-centro.webp"),
                Map.of(
                        "nombre", "Sede Lima Sur",
                        "direccion", "Ctra. Panamericana Sur km 16, Villa El Salvador 15842",
                        "telefono", "(01) 3159600",
                        "mapa",
                        "https://www.google.com/maps/place/Panamericana+Sur+Km+16,+Villa+El+Salvador+15842",
                        "imagen",
                        "https://utp.edu.pe/sites/default/files/campus/LimaSur-.webp"),
                Map.of(
                        "nombre", "Sede San Juan de Lurigancho",
                        "direccion", "Av. Wiesse 571, San Juan de Lurigancho 15434",
                        "telefono", "(01) 3159600",
                        "mapa", "https://maps.app.goo.gl/REhYfwjyYBLGhErc6",
                        "imagen", "https://utp.edu.pe/sites/default/files/campus/SJL-.webp"),
                Map.of(
                        "nombre", "Sede Ate",
                        "direccion", "Av. Carretera Central 123, Ate, Lima",
                        "telefono", "(01) 3159600",
                        "mapa", "https://maps.app.goo.gl/m6p3aiWzAuyf8mLu7",
                        "imagen",
                        "https://utp.edu.pe/sites/default/files/campus/utp-sede-ate.webp"));

        model.addAttribute("sedes", sedes);

        return "pages/sedes";
    }

    @GetMapping("/ventas")
    public String ventas() {
        return "pages/shop";
    }

    @GetMapping("/auth/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error,
                            Principal principal,
                            Model model) {
        if (principal != null) {
            return "redirect:/"; // Si el usuario ya está autenticado, redirigir a la página de inicio
        }
        if ("expired".equals(error)) {
            model.addAttribute("error", "Tu sesión ha expirado. Por favor, inicia sesión nuevamente.");
        }
        return "auth/login";
    }

    @GetMapping("/auth/register")
    public String registerPage(Model model) {
        model.addAttribute("usuario", new Usuario()); // Add an empty User object to the model
        return "auth/register";
    }
}