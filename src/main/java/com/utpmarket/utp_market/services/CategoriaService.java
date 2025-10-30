package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.CategoriaDTO;
import com.utpmarket.utp_market.models.entity.product.Categoria;
import com.utpmarket.utp_market.repository.CategoriaRepository;
import com.utpmarket.utp_market.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<CategoriaDTO> findAllCategoriasWithProductCount() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream().map(categoria -> {
            Long productCount = productoRepository.countByCategoria(categoria);
            return new CategoriaDTO(
                    categoria.getId(),
                    categoria.getNombre(),
                    categoria.getDescripcion(),
                    categoria.getIcono(),
                    productCount
            );
        }).collect(Collectors.toList());
    }
}
