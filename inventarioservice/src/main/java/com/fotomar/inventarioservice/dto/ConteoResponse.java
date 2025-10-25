package com.fotomar.inventarioservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConteoResponse {
    private String mensaje;
    private String sku;
    private String codigoUbicacion;
    private Integer cantidadSistema;
    private Integer cantidadFisica;
    private Integer diferencia;
    private Boolean hayDiferencia;
    private String tipoAlerta;
}