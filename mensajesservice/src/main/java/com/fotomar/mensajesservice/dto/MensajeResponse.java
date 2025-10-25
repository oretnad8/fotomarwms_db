package com.fotomar.mensajesservice.dto;

import com.fotomar.mensajesservice.model.Mensaje;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MensajeResponse {
    private Integer id;
    private EmisorDTO emisor;
    private DestinatarioDTO destinatario;
    private String titulo;
    private String contenido;
    private LocalDateTime fecha;
    private Boolean leido;
    private Boolean importante;
    private Boolean esParaTodos;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmisorDTO {
        private Integer id;
        private String nombre;
        private String email;
        private String rol;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DestinatarioDTO {
        private Integer id;
        private String nombre;
        private String email;
        private String rol;
    }
    
    public static MensajeResponse fromEntity(Mensaje mensaje) {
        MensajeResponseBuilder builder = MensajeResponse.builder()
                .id(mensaje.getId())
                .titulo(mensaje.getTitulo())
                .contenido(mensaje.getContenido())
                .fecha(mensaje.getFecha())
                .leido(mensaje.getLeido())
                .importante(mensaje.getImportante())
                .esParaTodos(mensaje.getDestinatario() == null);
        
        if (mensaje.getEmisor() != null) {
            builder.emisor(EmisorDTO.builder()
                    .id(mensaje.getEmisor().getId())
                    .nombre(mensaje.getEmisor().getNombre())
                    .email(mensaje.getEmisor().getEmail())
                    .rol(mensaje.getEmisor().getRol().name())
                    .build());
        }
        
        if (mensaje.getDestinatario() != null) {
            builder.destinatario(DestinatarioDTO.builder()
                    .id(mensaje.getDestinatario().getId())
                    .nombre(mensaje.getDestinatario().getNombre())
                    .email(mensaje.getDestinatario().getEmail())
                    .rol(mensaje.getDestinatario().getRol().name())
                    .build());
        }
        
        return builder.build();
    }
}