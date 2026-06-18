package com.proyectofullstack.prueba.Strategy;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TarjetaCreditoPago implements PagoStrategy{

    @Override
    public void procesarPago(Double monto){
        log.info("Procesando pago de ${} con tarjeta de crédito.", monto);
    }  
}
