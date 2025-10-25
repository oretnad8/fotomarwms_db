package com.fotomar.inventarioservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalizarInventarioResponse {
    private String mensaje;
    private LocalDateTime fechaFinalizacion;
    private Long totalDiferencias;
    private Long totalFaltantes;
    private Long totalSobrantes;
    private Integer productosAjustados;
    private String estado;
}