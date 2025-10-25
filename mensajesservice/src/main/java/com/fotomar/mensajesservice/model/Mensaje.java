package com.fotomar.mensajesservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "id_emisor", nullable = false)
    private Usuario emisor;
    
    @ManyToOne
    @JoinColumn(name = "id_destinatario")
    private Usuario destinatario;
    
    @Column(length = 100, nullable = false)
    private String titulo;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha;
    
    @Column(nullable = false)
    private Boolean leido = false;
    
    @Column(nullable = false)
    private Boolean importante = false;
}