package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.CategoriaDTO;
import com.utpmarket.utp_market.repository.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> findAllCategoriasWithProductCount() {
        return categoriaRepository.findAllCategoriasWithProductCount();
    }
}