package com.fotomar.mensajesservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResumenDTO {
    private Long totalMensajes;
    private Long mensajesNoLeidos;
    private Long mensajesImportantes;
    private Long mensajesImportantesNoLeidos;
}