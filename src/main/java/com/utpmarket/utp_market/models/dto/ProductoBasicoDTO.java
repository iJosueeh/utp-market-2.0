package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoBasicoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    // Potentially add image URL if available in Producto entity
    // private String imagenUrl;
}
