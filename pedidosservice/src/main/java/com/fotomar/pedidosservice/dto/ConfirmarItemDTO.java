package com.fotomar.pedidosservice.dto;

import lombok.Data;

@Data
public class ConfirmarItemDTO {
    private String sku;
    private Integer cantidad;
    private String codigoUbicacion;
}
