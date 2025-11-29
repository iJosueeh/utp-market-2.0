package com.utpmarket.utp_market.models.dto;

import lombok.Data;

@Data
public class ProductoAdminDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private Integer stock;
    private String categoria;
    private String estado;
    private String vendedor;
    private String imagenUrl;
}
