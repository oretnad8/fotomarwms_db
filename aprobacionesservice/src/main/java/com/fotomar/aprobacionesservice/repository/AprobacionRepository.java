package com.fotomar.aprobacionesservice.repository;

import com.fotomar.aprobacionesservice.model.Aprobacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AprobacionRepository extends JpaRepository<Aprobacion, Integer> {
    List<Aprobacion> findByEstadoOrderByFechaSolicitudDesc(Aprobacion.Estado estado);
    List<Aprobacion> findAllByOrderByFechaSolicitudDesc();
    List<Aprobacion> findBySolicitanteIdOrderByFechaSolicitudDesc(Integer solicitanteId);
}