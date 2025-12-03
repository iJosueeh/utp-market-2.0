package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Long id;
    private String numeroPedido;
    private Timestamp fechaPedido;
    private Double total;
    private EstadoPedidoDTO estado;
    private UsuarioDTO usuario;
}
