package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.ProductoDTO;
import com.utpmarket.utp_market.models.entity.user.Favorito;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.models.entity.product.Producto;
import com.utpmarket.utp_market.repository.FavoritoRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FavoritoService {

    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ProductoService productoService;

    @Transactional
    public boolean toggleFavorito(Long userId, Long productId) {
        System.out.println("FavoritoService: toggleFavorito called with userId = " + userId + ", productId = " + productId);
        UsuarioProductoPair pair = obtenerUsuarioYProducto(userId, productId);
        Usuario usuario = pair.usuario;
        Producto producto = pair.producto;

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
        try {
            UsuarioProductoPair pair = obtenerUsuarioYProducto(userId, productId);
            return favoritoRepository.findByUsuarioAndProducto(pair.usuario, pair.producto).isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public List<Producto> getFavoritosByUsuario(Long userId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        if (usuarioOpt.isEmpty()) {
            throw new IllegalArgumentException("Usuario no encontrado.");
        }
        Usuario usuario = usuarioOpt.get();
        return favoritoRepository.findAllByUsuario(usuario).stream()
                .map(Favorito::getProducto)
                .collect(Collectors.toList());
    }

    public List<ProductoDTO> getFavoritosByUsuarioDTO(Long userId) {
        List<Producto> productos = getFavoritosByUsuario(userId);
        return productos.stream()
                .map(productoService::convertToDto)
                .collect(Collectors.toList());
    }

    private UsuarioProductoPair obtenerUsuarioYProducto(Long userId, Long productId) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(userId);
        Optional<Producto> productoOpt = productoRepository.findById(productId);

        if (usuarioOpt.isEmpty() || productoOpt.isEmpty()) {
            System.err.println("FavoritoService: Usuario o Producto no encontrado. userId = " + userId + ", productId = " + productId);
            throw new IllegalArgumentException("Usuario o Producto no encontrado.");
        }
        return new UsuarioProductoPair(usuarioOpt.get(), productoOpt.get());
    }

    private static class UsuarioProductoPair {
        final Usuario usuario;
        final Producto producto;

        UsuarioProductoPair(Usuario usuario, Producto producto) {
            this.usuario = usuario;
            this.producto = producto;
        }
    }
}