package com.proyectofullstack.prueba.dto;

// DTO de SALIDA — lo que la API devuelve al frontend o a otros microservicios.
// Sí incluye el id porque el producto ya existe en la BD.
// NO expone campos internos como secuencias, versiones o datos sensibles.
public class ProductoResponseDTO {

    private Long id;
    private String nombre;
    private Integer stock;
    private Double precio;
    private String estadoStock; // Campo calculado: "DISPONIBLE", "CRÍTICO" o "AGOTADO"

    // Constructor vacío requerido por Jackson.
    public ProductoResponseDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }

    public String getEstadoStock() { return estadoStock; }
    public void setEstadoStock(String estadoStock) { this.estadoStock = estadoStock; }
}