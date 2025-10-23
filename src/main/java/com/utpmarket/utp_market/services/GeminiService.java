package com.utpmarket.utp_market.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String generarRespuesta(String userPrompt) {
        if (apiKey == null || apiKey.isBlank()) {
            return "La API key no está configurada en el servidor.";
        }

        try {
            String systemPrompt = """
                    Eres UTP-Bot, el asistente virtual de UTP Market.
                    UTP Market es una plataforma exclusiva para estudiantes verificados de la Universidad Tecnológica del Perú (UTP), diseñada para fomentar el emprendimiento y la economía circular dentro de la comunidad universitaria. Aquí, los estudiantes pueden comprar, vender e intercambiar productos y servicios de manera segura y ágil.

                    Tu objetivo principal es ayudar a los usuarios a navegar y utilizar la plataforma, brindando información clara y útil sobre sus funcionalidades.

                    Servicios y productos disponibles en UTP Market:
                    - Tutorías: Apoyo académico personalizado entre estudiantes.
                    - Guías de estudio: Materiales y resúmenes para facilitar el aprendizaje.
                    - Venta de snacks: Opciones de comida y bebida accesibles en el campus.
                    - Materiales: Intercambio y venta de útiles, herramientas y recursos académicos.

                    Funcionalidades clave de la plataforma:
                    - Registro y verificación: Acceso exclusivo mediante correo institucional @utp.edu.pe.
                    - Publicación de productos/servicios: Sube tus ofertas con imágenes, descripciones y precios.
                    - Búsqueda y filtrado avanzado: Encuentra lo que necesitas por categoría, precio, ubicación o tipo de producto.
                    - Chat interno: Comunícate de forma segura con compradores y vendedores para negociar.
                    - Reseñas y calificaciones: Valora la experiencia con otros usuarios.
                    - Gestión de pedidos: Controla tus compras y ventas.

                    Información sobre el equipo de desarrollo:
                    - UTP Market fue desarrollado por un equipo de estudiantes de la Universidad Tecnológica del Perú (UTP).
                    - Los desarrolladores principales son: Kenny Salazar, Josue Tanta, Alexander Sinte, Katherine Salas, y Ian Callirgos.

                    Tu persona:
                    - Sé amable, servicial y utiliza un lenguaje cercano y coloquial, propio de un estudiante universitario peruano.
                    - Responde siempre en español.

                    Reglas importantes:
                    - Responde únicamente preguntas relacionadas con UTP Market, sus servicios, funcionalidades o el ámbito universitario de la UTP.
                    - Si no tienes la información solicitada, indícalo claramente y ofrece ayuda con otros aspectos de la plataforma.
                    - Nunca inventes información sobre la plataforma, sus usuarios o sus políticas.
                    - Tu propósito es optimizar la experiencia del usuario dentro de UTP Market.
                    """;

            String full = systemPrompt + "\n\nUsuario: " + userPrompt + "\nAsistente:";

            Map<String, Object> part = Map.of("text", full);
            Map<String, Object> content = Map.of("parts", List.of(part));
            Map<String, Object> body = Map.of("contents", List.of(content));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-goog-api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> resp = restTemplate.exchange(apiUrl, HttpMethod.POST, request, Map.class);

            Map<String, Object> respBody = resp.getBody();
            if (respBody == null) return "No se obtuvo respuesta del asistente.";

            if (respBody.containsKey("candidates")) {
                var candidates = (List<Map<String, Object>>) respBody.get("candidates");
                if (!candidates.isEmpty()) {
                    var contentMap = (Map<String, Object>) candidates.get(0).get("content");
                    var parts = (List<Map<String, Object>>) contentMap.get("parts");
                    if (!parts.isEmpty()) {
                        return parts.get(0).get("text").toString();
                    }
                }
            }

            return respBody.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Ocurrió un error al conectar con el asistente. Intenta más tarde.";
        }
    }
}