package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.entity.user.Cita;
import com.utpmarket.utp_market.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitaService {

    @Autowired
    private CitaRepository citaRepository;

    public void guardarCita(Cita cita) {
        citaRepository.save(cita);
    }
}
