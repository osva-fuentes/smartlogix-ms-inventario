package com.proyectofullstack.prueba.adapter;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MercadoLibreAdapter implements MarketplaceAdapter{

    @Override
    public void sincronizarStockExterna(Long idProducto, Integer nuevoStock){
        log.info("Transformando datos de SmartLogix al formato de la API de MercadoLibre... Producto {} actualizado a {} unidades.", idProducto, nuevoStock);
    }
}
