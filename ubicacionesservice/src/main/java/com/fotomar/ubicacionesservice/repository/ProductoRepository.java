package com.fotomar.ubicacionesservice.repository;

import com.fotomar.ubicacionesservice.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, String> {
}