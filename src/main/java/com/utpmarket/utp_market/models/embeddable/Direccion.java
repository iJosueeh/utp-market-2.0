package com.utpmarket.utp_market.models.embeddable;

import jakarta.persistence.Embeddable;

@Embeddable
public class Direccion {

    private String calle;
    private String distrito;

    public Direccion() {}

    public Direccion(String calle, String distrito) {
        this.calle = calle;
        this.distrito = distrito;
    }

    public String getCalle() {
        return calle;
    }

    public void setCalle(String calle) {
        this.calle = calle;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }
}