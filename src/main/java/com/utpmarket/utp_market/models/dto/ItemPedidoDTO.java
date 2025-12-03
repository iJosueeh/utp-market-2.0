package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemPedidoDTO {
    private Long id;
    private ProductoBasicoDTO producto;
    private Integer cantidad;
    private Double precioUnitario;
    private Double subtotal;
}
