package com.fotomar.inventarioservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoResponse {
    private Long totalUbicaciones;
    private Long ubicacionesContadas;
    private Long ubicacionesPendientes;
    private Double porcentajeCompletado;
    private Long totalDiferenciasRegistradas;
    private Long totalFaltantes;
    private Long totalSobrantes;
    private Long ubicacionesConDiferencias;
}