package com.utpmarket.utp_market.controllers;

import com.utpmarket.utp_market.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
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
        return ResponseEntity.ok(new ChatResponse(reply));
    }

}