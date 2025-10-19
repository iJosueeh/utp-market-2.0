package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.Carrito;
import com.utpmarket.utp_market.models.entity.Producto;
import com.utpmarket.utp_market.models.entity.Usuario;
import com.utpmarket.utp_market.repository.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    public void agregarCarrito(Usuario cliente, Producto producto, int cantidad) {
        Carrito item = new Carrito();
        item.setCliente(cliente);
        item.setProducto(producto);
        item.setCantidad(cantidad);
        carritoRepository.save(item);
    }

    public List<Carrito> obtenerCarritoPorCliente(Usuario cliente) {
        return carritoRepository.findByCliente(cliente);
    }

    public void eliminarDelCarrito(Long id) {
        carritoRepository.deleteById(id);
    }

}