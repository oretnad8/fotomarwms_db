package com.fotomar.productosservice.repository;

import com.fotomar.productosservice.model.ProductoUbicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductoUbicacionRepository extends JpaRepository<ProductoUbicacion, Integer> {
    Optional<ProductoUbicacion> findByProductoSkuAndUbicacionIdUbicacion(String sku, Integer idUbicacion);
}