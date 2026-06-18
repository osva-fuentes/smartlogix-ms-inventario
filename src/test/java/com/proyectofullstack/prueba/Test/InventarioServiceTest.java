package com.proyectofullstack.prueba.Test;

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

    @Test
    void testDescontarStock_ConExito() {
        // Arrange
        Inventario mock = new Inventario();
        mock.setId(1L);
        mock.setStock(20);
        when(repository.findById(1L)).thenReturn(Optional.of(mock));

        // Act
        boolean resultado = service.descontarStock(1L, 5);

        // Assert
        assertTrue(resultado);
        assertEquals(15, mock.getStock());
        verify(repository, times(1)).save(mock);
        // Verificamos que el Adapter también se haya ejecutado en la prueba
        verify(adapter, times(1)).sincronizarStockExterna(1L, 15);
    }
}
