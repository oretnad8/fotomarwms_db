package com.fotomar.mensajesservice.repository;

import com.fotomar.mensajesservice.model.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Integer> {
    
    @Query("SELECT m FROM Mensaje m WHERE m.destinatario.id = :userId OR m.destinatario IS NULL ORDER BY m.fecha DESC")
    List<Mensaje> findMensajesParaUsuario(@Param("userId") Integer userId);
    
    @Query("SELECT m FROM Mensaje m WHERE (m.destinatario.id = :userId OR m.destinatario IS NULL) AND m.leido = false ORDER BY m.fecha DESC")
    List<Mensaje> findMensajesNoLeidosParaUsuario(@Param("userId") Integer userId);
    
    @Query("SELECT m FROM Mensaje m WHERE (m.destinatario.id = :userId OR m.destinatario IS NULL) AND m.importante = true ORDER BY m.fecha DESC")
    List<Mensaje> findMensajesImportantesParaUsuario(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE (m.destinatario.id = :userId OR m.destinatario IS NULL)")
    Long countMensajesParaUsuario(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE (m.destinatario.id = :userId OR m.destinatario IS NULL) AND m.leido = false")
    Long countMensajesNoLeidosParaUsuario(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE (m.destinatario.id = :userId OR m.destinatario IS NULL) AND m.importante = true")
    Long countMensajesImportantesParaUsuario(@Param("userId") Integer userId);
    
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE (m.destinatario.id = :userId OR m.destinatario IS NULL) AND m.importante = true AND m.leido = false")
    Long countMensajesImportantesNoLeidosParaUsuario(@Param("userId") Integer userId);
    
    List<Mensaje> findByEmisorIdOrderByFechaDesc(Integer emisorId);
}