package com.proyectofullstack.prueba.Repository;

import com.proyectofullstack.prueba.Entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
}
