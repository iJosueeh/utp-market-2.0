package com.utpmarket.utp_market.services;

import com.utpmarket.utp_market.models.dto.Contacto;
import org.springframework.stereotype.Service;

@Service
public class ContactoService {

    public void procesarContacto(Contacto contacto) {
        System.out.println("Contacto recibido y procesado en el servicio:");
        System.out.println("Categoría: " + contacto.getCategoria());
        System.out.println("Nombre: " + contacto.getNombre());
        System.out.println("Correo: " + contacto.getCorreo());
        System.out.println("Mensaje: " + contacto.getMensaje());
    }
}