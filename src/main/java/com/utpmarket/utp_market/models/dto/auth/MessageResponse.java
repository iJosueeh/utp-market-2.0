package com.utpmarket.utp_market.models.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para respuestas de mensajes generales
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
