package com.proyectofullstack.prueba.Event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificacionStockListener {

    @EventListener
    public void manejarStockBajo(StockBajoEvent event) {
        log.error("Enviando correo automático a Bodega: ¡El producto '{}' está en nivel CRÍTICO y necesita reposición inmediata!",
                event.getNombreProducto());
    }
}