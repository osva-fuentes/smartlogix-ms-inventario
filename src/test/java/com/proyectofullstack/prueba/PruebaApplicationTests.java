package com.proyectofullstack.prueba;

import com.proyectofullstack.prueba.Entity.Inventario;
import com.proyectofullstack.prueba.Repository.InventarioRepository;
import com.proyectofullstack.prueba.Service.InventarioService;
import com.proyectofullstack.prueba.adapter.MarketplaceAdapter;
import org.springframework.context.ApplicationEventPublisher;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository repository;

    @Mock
    private MarketplaceAdapter adapter;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventarioService service;

    // PRUEBA 1: Descuento exitoso 
    @Test
    void testDescontarStock_ConExito() {
        // Arrange — preparamos un producto con stock 20
        Inventario mock = new Inventario();
        mock.setId(1L);
        mock.setNombre("Producto de Prueba");
        mock.setStock(20);
        when(repository.findById(1L)).thenReturn(Optional.of(mock));

        // Act — descontamos 5 unidades
        boolean resultado = service.descontarStock(1L, 5);

        // Assert — verificamos resultado y efectos secundarios
        assertTrue(resultado);
        assertEquals(15, mock.getStock());
        verify(repository, times(1)).save(mock);
        verify(adapter, times(1)).sincronizarStockExterna(1L, 15);
    }

    // PRUEBA 2: Stock insuficiente 
    @Test
    void testDescontarStock_StockInsuficiente() {
        // Arrange — producto con solo 3 unidades
        Inventario mock = new Inventario();
        mock.setId(1L);
        mock.setNombre("Producto de Prueba");
        mock.setStock(3);
        when(repository.findById(1L)).thenReturn(Optional.of(mock));

        // Act — intentamos descontar 10 unidades (más de lo disponible)
        boolean resultado = service.descontarStock(1L, 10);

        // Assert — debe fallar y no modificar nada
        assertFalse(resultado);
        assertEquals(3, mock.getStock());
        verify(repository, never()).save(any());
    }

    // ── PRUEBA 3: Observer se activa con stock crítico 
    @Test
    void testDescontarStock_ActivaObserverEnStockCritico() {
        // Arrange — producto con 5 unidades, al descontar 1 queda en 4 (< 5)
        Inventario mock = new Inventario();
        mock.setId(1L);
        mock.setNombre("Producto de Prueba");
        mock.setStock(5);
        when(repository.findById(1L)).thenReturn(Optional.of(mock));

        // Act
        boolean resultado = service.descontarStock(1L, 1);

        // Assert — el Observer debe haber sido notificado
        assertTrue(resultado);
        assertEquals(4, mock.getStock());
        verify(eventPublisher, times(1)).publishEvent(any());
    }

	// PRUEBA 4: Producto no encontrado retorna false
    @Test
    void testDescontarStock_ProductoNoEncontrado() {
        // Arrange — el repositorio no encuentra el producto
        when(repository.findById(999L)).thenReturn(Optional.empty());

        // Act
        boolean resultado = service.descontarStock(999L, 1);

        // Assert
        assertFalse(resultado);
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
}