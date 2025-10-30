package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.user.Favorito;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.repository.FavoritoRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Transactional
    public boolean toggleFavorito(Long userId, Long productId) {
        System.out.println("FavoritoService: toggleFavorito called with userId = " + userId + ", productId = " + productId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        Optional<Producto> productoOpt = productoRepository.findById(productId);

        if (usuarioOpt.isEmpty() || productoOpt.isEmpty()) {
            System.err.println("FavoritoService: Usuario o Producto no encontrado. userId = " + userId + ", productId = " + productId);
            throw new IllegalArgumentException("Usuario o Producto no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        Producto producto = productoOpt.get();

        Optional<Favorito> favoritoOpt = favoritoRepository.findByUsuarioAndProducto(usuario, producto);

        if (favoritoOpt.isPresent()) {
            favoritoRepository.deleteByUsuarioAndProducto(usuario, producto);
            return false; // Eliminado de favoritos
        } else {
            Favorito nuevoFavorito = new Favorito();
            nuevoFavorito.setUsuario(usuario);
            nuevoFavorito.setProducto(producto);
            favoritoRepository.save(nuevoFavorito);
            return true; // Añadido a favoritos
        }
    }

    public boolean isFavorito(Long userId, Long productId) {
        System.out.println("FavoritoService: isFavorito called with userId = " + userId + ", productId = " + productId);
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        Optional<Producto> productoOpt = productoRepository.findById(productId);

        if (usuarioOpt.isEmpty() || productoOpt.isEmpty()) {
            System.err.println("FavoritoService: Usuario o Producto no encontrado. userId = " + userId + ", productId = " + productId);
            return false; // No puede ser favorito si no existen usuario o producto
        }

        return favoritoRepository.findByUsuarioAndProducto(usuarioOpt.get(), productoOpt.get()).isPresent();
    }
}
