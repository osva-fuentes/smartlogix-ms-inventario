package com.proyectofullstack.prueba.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Entity
@Data
public class Inventario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inv_seq")
    @SequenceGenerator(name = "inv_seq", sequenceName = "INVENTARIO_SEQ", allocationSize = 1)
    private Long id;
    private String nombre;
    private Integer stock;
    private Double precio;
}
