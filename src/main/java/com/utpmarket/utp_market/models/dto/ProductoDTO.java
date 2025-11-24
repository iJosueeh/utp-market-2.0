package com.utpmarket.utp_market.models.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private String estadoNombre;
    private Integer stock;
    private Long categoriaId;
    private String categoriaNombre;
    private Long vendedorId;
    private String vendedorNombre;
    private Boolean isDestacado;
    private Timestamp fechaCreacion;
    private String imagenUrlPrincipal;
    private Double rating;
    private Integer numReviews;
    private Double descuento;
    private Boolean isNuevo;
    private Double precioAnterior;
}