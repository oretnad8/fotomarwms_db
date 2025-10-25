package com.fotomar.inventarioservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_diferencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioDiferencia {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(length = 7, nullable = false)
    private String sku;
    
    @Column(name = "id_ubicacion", nullable = false)
    private Integer idUbicacion;
    
    @Column(name = "cantidad_sistema", nullable = false)
    private Integer cantidadSistema;
    
    @Column(name = "cantidad_fisica", nullable = false)
    private Integer cantidadFisica;
    
    @Column(nullable = false)
    private Integer diferencia;
    
    @CreationTimestamp
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    @ManyToOne
    @JoinColumn(name = "id_registrador", nullable = false)
    private Usuario registrador;
}