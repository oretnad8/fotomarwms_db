package com.fotomar.inventarioservice.dto;

import com.fotomar.inventarioservice.model.InventarioDiferencia;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiferenciaResponse {
    private Integer id;
    private String sku;
    private String descripcionProducto;
    private Integer idUbicacion;
    private String codigoUbicacion;
    private Integer cantidadSistema;
    private Integer cantidadFisica;
    private Integer diferencia;
    private String tipoDiferencia;
    private LocalDateTime fechaRegistro;
    private String nombreRegistrador;
    
    public static DiferenciaResponse fromEntity(InventarioDiferencia diferencia, String descripcionProducto, String codigoUbicacion) {
        String tipoDiferencia;
        if (diferencia.getDiferencia() > 0) {
            tipoDiferencia = "SOBRANTE";
        } else if (diferencia.getDiferencia() < 0) {
            tipoDiferencia = "FALTANTE";
        } else {
            tipoDiferencia = "CORRECTO";
        }
        
        return DiferenciaResponse.builder()
                .id(diferencia.getId())
                .sku(diferencia.getSku())
                .descripcionProducto(descripcionProducto)
                .idUbicacion(diferencia.getIdUbicacion())
                .codigoUbicacion(codigoUbicacion)
                .cantidadSistema(diferencia.getCantidadSistema())
                .cantidadFisica(diferencia.getCantidadFisica())
                .diferencia(diferencia.getDiferencia())
                .tipoDiferencia(tipoDiferencia)
                .fechaRegistro(diferencia.getFechaRegistro())
                .nombreRegistrador(diferencia.getRegistrador().getNombre())
                .build();
    }
}