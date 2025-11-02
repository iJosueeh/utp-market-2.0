package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.VendedorDTO;
import com.utpmarket.utp_market.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<VendedorDTO> findAllVendedoresWithProductCount() {
        return usuarioRepository.findAllVendedoresWithProductCount();
    }
}