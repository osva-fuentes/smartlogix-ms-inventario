package com.proyectofullstack.prueba.Controller;

import com.proyectofullstack.prueba.dto.ProductoRequestDTO;
import com.proyectofullstack.prueba.dto.ProductoResponseDTO;
import com.proyectofullstack.prueba.Factory.PagoFactory;
import com.proyectofullstack.prueba.Strategy.PagoStrategy;
import com.proyectofullstack.prueba.Service.InventarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    private final InventarioService service;

    InventarioController(InventarioService service) {
        this.service = service;
    }

    // LISTAR TODOS 
    // Devuelve lista de ProductoResponseDTO — nunca expone la Entity
    @GetMapping("/todos")
    public ResponseEntity<List<ProductoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(service.listarTodos());
    }

    // BUSCAR POR ID 
    // Devuelve un ProductoResponseDTO con estadoStock calculado
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // CREAR PRODUCTO 
    // Recibe ProductoRequestDTO (sin id), devuelve ProductoResponseDTO (con id)
    @PostMapping("/crear")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ProductoResponseDTO> crear(@RequestBody ProductoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(dto));
    }

    // DESCONTAR STOCK
    // Lógica original intacta — Strategy + Singleton + Adapter + Observer
    @PostMapping("/descontar/{id}")
    public ResponseEntity<String> descontar(
            @PathVariable("id") @NonNull Long id,
            @RequestParam("cantidad") Integer cantidad) {

        // Strategy Pattern — procesa el pago antes de descontar stock
        PagoStrategy metodoPago = PagoFactory.getMetodoPago("Tarjeta");
        if (metodoPago != null) {
            metodoPago.procesarPago(15000.0);
        }

        // Singleton + Adapter + Observer — lógica de inventario
        boolean exito = service.descontarStock(id, cantidad);

        if (exito) {
            return ResponseEntity.ok("Compra exitosa");
        } else {
            return ResponseEntity.badRequest().body("Stock insuficiente o producto no encontrado");
        }
    }
}