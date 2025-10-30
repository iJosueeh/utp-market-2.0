package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.VendedorDTO;
import com.utpmarket.utp_market.models.entity.user.Usuario;
import com.utpmarket.utp_market.repository.ProductoRepository;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<VendedorDTO> findAllVendedoresWithProductCount() {
        List<Usuario> vendedores = usuarioRepository.findByRol_Nombre("vendedor");

        return vendedores.stream().map(vendedor -> {
            Long productCount = productoRepository.countByVendedor(vendedor);
            return new VendedorDTO(
                    vendedor.getId(),
                    vendedor.getNombre(),
                    productCount
            );
        }).collect(Collectors.toList());
    }
}
