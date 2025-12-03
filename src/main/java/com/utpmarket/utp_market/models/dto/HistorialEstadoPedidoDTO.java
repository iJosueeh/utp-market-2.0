package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistorialEstadoPedidoDTO {
    private Long id;
    private Timestamp fechaCambio;
    private EstadoPedidoDTO estadoAnterior;
    private EstadoPedidoDTO estadoNuevo;
    private UsuarioDTO usuarioResponsable;
}
