package com.fotomar.inventarioservice.repository;

import com.fotomar.inventarioservice.model.InventarioDiferencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioDiferenciaRepository extends JpaRepository<InventarioDiferencia, Integer> {
    
    List<InventarioDiferencia> findAllByOrderByFechaRegistroDesc();
    
    @Query("SELECT COUNT(DISTINCT id.idUbicacion) FROM InventarioDiferencia id")
    Long countUbicacionesContadas();
    
    @Query("SELECT COUNT(id) FROM InventarioDiferencia id WHERE id.diferencia < 0")
    Long countFaltantes();
    
    @Query("SELECT COUNT(id) FROM InventarioDiferencia id WHERE id.diferencia > 0")
    Long countSobrantes();
    
    @Query("SELECT COUNT(DISTINCT id.idUbicacion) FROM InventarioDiferencia id WHERE id.diferencia != 0")
    Long countUbicacionesConDiferencias();
    
    List<InventarioDiferencia> findByDiferenciaNot(Integer diferencia);
}