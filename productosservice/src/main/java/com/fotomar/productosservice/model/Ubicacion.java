package com.fotomar.productosservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ubicaciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ubicacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ubicacion")
    private Integer idUbicacion;
    
    @Column(name = "codigo_ubicacion", length = 10, unique = true, nullable = false)
    private String codigoUbicacion;
    
    @Column(length = 1, nullable = false)
    private Character piso;
    
    @Column(nullable = false)
    private Integer numero;
}