package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VendedorDTO {
    private Long id;
    private String nombre;
    private Long cantidadProductos;
}
