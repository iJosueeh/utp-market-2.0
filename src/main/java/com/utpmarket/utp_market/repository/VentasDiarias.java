package com.utpmarket.utp_market.repository;

import java.time.LocalDate;

public interface VentasDiarias {
    LocalDate getFecha();
    Double getTotal();
}