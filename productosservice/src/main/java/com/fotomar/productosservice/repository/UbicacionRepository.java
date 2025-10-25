package com.fotomar.productosservice.repository;

import com.fotomar.productosservice.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    Optional<Ubicacion> findByCodigoUbicacion(String codigoUbicacion);
}