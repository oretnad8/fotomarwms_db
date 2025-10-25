package com.fotomar.inventarioservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConteoRequest {
    
    @NotBlank(message = "El SKU es obligatorio")
    private String sku;
    
    @NotNull(message = "El ID de ubicación es obligatorio")
    private Integer idUbicacion;
    
    @NotNull(message = "La cantidad física es obligatoria")
    @Min(value = 0, message = "La cantidad física debe ser mayor o igual a 0")
    private Integer cantidadFisica;
}