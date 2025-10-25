package com.fotomar.ubicacionesservice.repository;

import com.fotomar.ubicacionesservice.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    Optional<Ubicacion> findByCodigoUbicacion(String codigoUbicacion);
    List<Ubicacion> findByPisoOrderByNumero(Character piso);
    boolean existsByCodigoUbicacion(String codigoUbicacion);
}