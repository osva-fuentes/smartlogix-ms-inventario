package com.proyectofullstack.prueba.Factory;

import com.proyectofullstack.prueba.Strategy.PagoStrategy;
import com.proyectofullstack.prueba.Strategy.TarjetaCreditoPago;

public class PagoFactory {
    public static PagoStrategy getMetodoPago(String tipo){
        if (tipo.equalsIgnoreCase("Tarjeta")){
            return new TarjetaCreditoPago();
        }
        //Aqui se pueden agregar mas metodos de pago si el profe lo pide (Paypal, efectivo, etc)...
        return null;
    }
}
