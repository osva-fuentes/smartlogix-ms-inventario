package com.proyectofullstack.prueba.Service;

import jakarta.annotation.PostConstruct;
import com.proyectofullstack.prueba.Entity.Inventario;
import com.proyectofullstack.prueba.Event.StockBajoEvent;
import com.proyectofullstack.prueba.Repository.InventarioRepository;
import com.proyectofullstack.prueba.adapter.MarketplaceAdapter;
import com.proyectofullstack.prueba.dto.ProductoRequestDTO;
import com.proyectofullstack.prueba.dto.ProductoResponseDTO;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class InventarioService {

    private final InventarioRepository repository;

    private final ApplicationEventPublisher eventPublisher;

    private final MarketplaceAdapter adapter;

    // Patrón Singleton — Spring solo crea una instancia de este Service
    public InventarioService(InventarioRepository repository, ApplicationEventPublisher eventPublisher, MarketplaceAdapter adapter) {
        log.info("Patrón Singleton ejecutado. HashCode: {}", this.hashCode());
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.adapter = adapter;
    }

    @PostConstruct
    public void inicializarBaseDeDatos() {
        if (repository.count() == 0) {
            Inventario productoPrueba = new Inventario();
            productoPrueba.setNombre("Producto de Prueba");
            productoPrueba.setStock(20);
            productoPrueba.setPrecio(15000.0);
            repository.save(productoPrueba);
            log.info("Base de datos inicializada. Producto ID 1 creado con 20 unidades.");
        }
    }

    // LISTAR TODOS 
    // Devuelve una lista de DTO en vez de Entities
    public List<ProductoResponseDTO> listarTodos() {
        return repository.findAll()
                .stream()
                .map(this::convertirAResponseDTO)
                .collect(Collectors.toList());
    }

    // BUSCAR POR ID 
    // Devuelve un DTO con el estado del stock calculado
    public ProductoResponseDTO buscarPorId(@NonNull Long id) {
        Inventario producto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con id: " + id));
        return convertirAResponseDTO(producto);
    }

    // CREAR PRODUCTO 
    // Recibe DTO de entrada, guarda Entity, devuelve DTO de salida
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Inventario entity = convertirAEntity(dto);
        Inventario guardado = repository.save(entity);
        log.info("Producto creado con ID: {}", guardado.getId());
        return convertirAResponseDTO(guardado);
    }

    // DESCONTAR STOCK 
    // Lógica original intacta — ahora devuelve DTO en vez de boolean
    public boolean descontarStock(@NonNull Long id, Integer cantidad) {
        log.info("Iniciando descuento de stock para producto ID: {}", id);

        Optional<Inventario> productoOpt = repository.findById(id);

        if (productoOpt.isPresent()) {
            Inventario producto = productoOpt.get();
            if (producto.getStock() >= cantidad) {
                producto.setStock(producto.getStock() - cantidad);
                repository.save(producto);

                log.info("Stock actualizado. Nuevo stock: {}", producto.getStock());

                // Patrón Adapter — sincroniza con plataformas externas
                adapter.sincronizarStockExterna(producto.getId(), producto.getStock());

                // Patrón Observer — alerta si stock es crítico
                if (producto.getStock() < 5) {
                    log.warn("Stock crítico para: {}", producto.getNombre());
                    eventPublisher.publishEvent(new StockBajoEvent(this, producto.getNombre()));
                }

                return true;
            }
            log.warn("Stock insuficiente para ID: {}", id);
        } else {
            log.error("Producto no encontrado con ID: {}", id);
        }
        return false;
    }

    // MÉTODOS PRIVADOS DE CONVERSIÓN

    // Convierte DTO de entrada → Entity para guardar en BD
    private Inventario convertirAEntity(ProductoRequestDTO dto) {
        Inventario entity = new Inventario();
        entity.setNombre(dto.getNombre());
        entity.setStock(dto.getStock());
        entity.setPrecio(dto.getPrecio());
        return entity;
    }

    // Convierte Entity → DTO de salida para responder al frontend
    // Aquí calculamos estadoStock, que NO existe en la tabla de BD
    private ProductoResponseDTO convertirAResponseDTO(Inventario entity) {
        ProductoResponseDTO dto = new ProductoResponseDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setStock(entity.getStock());
        dto.setPrecio(entity.getPrecio());

        // Campo calculado — demuestra el valor real del DTO
        if (entity.getStock() == 0) {
            dto.setEstadoStock("AGOTADO");
        } else if (entity.getStock() < 5) {
            dto.setEstadoStock("CRÍTICO");
        } else {
            dto.setEstadoStock("DISPONIBLE");
        }

        return dto;
    }
}