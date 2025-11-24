package com.utpmarket.utp_market.models.dto;

import lombok.Data;

@Data
public class Contacto {
    private String categoria;
    private String nombre;
    private String correo;
    private String mensaje;
}