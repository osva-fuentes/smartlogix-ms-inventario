package com.proyectofullstack.prueba.Event;

import org.springframework.context.ApplicationEvent;
import lombok.Getter;

@Getter
public class StockBajoEvent extends ApplicationEvent {
    private final String nombreProducto;

    public StockBajoEvent(Object source, String nombreProducto) {
        super(source);
        this.nombreProducto = nombreProducto;
    }
}