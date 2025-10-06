package com.utpmarket.utp_market.models.dto;

public record SolicitudRegistro(
        String nombre,
        String apellidos,
        String telefono,
        String email,
        String password
) {}
