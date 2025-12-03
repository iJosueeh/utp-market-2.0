package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDetalleDTO {
    private Long id;
    private String numeroPedido;
    private Timestamp fechaPedido;
    private Double total;
    private EstadoPedidoDTO estado;
    private UsuarioDTO usuario;
    private String transactionId;
    private DireccionDTO direccion;
    private MetodoPagoDTO metodoPago;
    private List<ItemPedidoDTO> items;
    private List<HistorialEstadoPedidoDTO> historialEstadoPedidos;
}
