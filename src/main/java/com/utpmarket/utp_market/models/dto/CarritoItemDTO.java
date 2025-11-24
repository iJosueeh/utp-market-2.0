package com.utpmarket.utp_market.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CarritoItemDTO {
    private Long id;
    private ProductoDTO producto;
    private int cantidad;
    private double subtotal;

    public CarritoItemDTO(Long id, ProductoDTO producto, int cantidad) {
        this.id = id;
        this.producto = producto;
        this.cantidad = cantidad;
        this.subtotal = producto.getPrecio() * cantidad;
    }
}
