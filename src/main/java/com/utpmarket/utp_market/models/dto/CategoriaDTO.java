package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String icono;
    private Long cantidadProductos;
}
