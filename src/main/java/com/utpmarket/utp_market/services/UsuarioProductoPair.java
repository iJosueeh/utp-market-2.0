package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.entity.product.Producto;

public class UsuarioProductoPair {
    public final Usuario usuario;
    public final Producto producto;

    public UsuarioProductoPair(Usuario usuario, Producto producto) {
        this.usuario = usuario;
        this.producto = producto;
    }
}
