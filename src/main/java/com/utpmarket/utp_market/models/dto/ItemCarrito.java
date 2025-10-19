package com.utpmarket.utp_market.models.dto;

import com.utpmarket.utp_market.models.entity.Producto;
import com.utpmarket.utp_market.models.entity.Usuario;

public record ItemCarrito(
        Producto producto,
        int cantidad,
        Usuario usuario) {}
