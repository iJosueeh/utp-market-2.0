package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

record ChatRequest(String message) {}
record ChatResponse(String reply) {}

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/ask")
    public ResponseEntity<ChatResponse> ask(@RequestBody ChatRequest req) {
        String reply = geminiService.generarRespuesta(req.message());

        if (reply.contains("La API key no está configurada")) {
             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                  .body(new ChatResponse("Error interno del servidor: La API key de Gemini no está configurada."));
        }
        if (reply.contains("No se obtuvo respuesta del asistente.")) {
             return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                  .body(new ChatResponse("El asistente no respondió. Inténtalo de nuevo."));
        }
        if (reply.contains("Ocurrió un error al conectar con el asistente.")) {
             return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                                  .body(new ChatResponse("Ocurrió un error al conectar con el asistente. Inténtalo más tarde."));
        }

        return ResponseEntity.ok(new ChatResponse(reply));
    }
}