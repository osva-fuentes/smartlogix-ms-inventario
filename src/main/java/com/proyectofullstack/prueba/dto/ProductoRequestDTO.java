package com.proyectofullstack.prueba.dto;

// DTO de ENTRADA para crear o actualizar un producto.
// Solo contiene los datos que el usuario puede enviar.
// NO incluye el id porque lo genera la base de datos.
public class ProductoRequestDTO {

    private String nombre;
    private Integer stock;
    private Double precio;

    // Constructor vacío requerido por Jackson para convertir JSON a objeto.
    public ProductoRequestDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public Double getPrecio() { return precio; }
    public void setPrecio(Double precio) { this.precio = precio; }
}