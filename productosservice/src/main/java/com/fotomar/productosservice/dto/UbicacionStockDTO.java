package com.fotomar.productosservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionStockDTO {
    private Integer idUbicacion;
    private String codigoUbicacion;
    private Character piso;
    private Integer numero;
    private Integer cantidad;
}