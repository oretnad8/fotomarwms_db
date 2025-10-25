package com.fotomar.ubicacionesservice.dto;

import com.fotomar.ubicacionesservice.model.Ubicacion;
import com.fotomar.ubicacionesservice.model.ProductoUbicacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UbicacionResponse {
    private Integer idUbicacion;
    private String codigoUbicacion;
    private Character piso;
    private Integer numero;
    private List<ProductoEnUbicacionDTO> productos;
    private Integer totalProductos;
    private Integer cantidadTotal;
    
    public static UbicacionResponse fromEntity(Ubicacion ubicacion) {
        List<ProductoEnUbicacionDTO> productosDTO = ubicacion.getProductos().stream()
                .map(pu -> ProductoEnUbicacionDTO.builder()
                        .sku(pu.getProducto().getSku())
                        .descripcion(pu.getProducto().getDescripcion())
                        .cantidadEnUbicacion(pu.getCantidadEnUbicacion())
                        .lpn(pu.getProducto().getLpn())
                        .vencimientoCercano(pu.getProducto().getVencimientoCercano())
                        .build())
                .collect(Collectors.toList());
        
        Integer cantidadTotal = ubicacion.getProductos().stream()
                .mapToInt(ProductoUbicacion::getCantidadEnUbicacion)
                .sum();
        
        return UbicacionResponse.builder()
                .idUbicacion(ubicacion.getIdUbicacion())
                .codigoUbicacion(ubicacion.getCodigoUbicacion())
                .piso(ubicacion.getPiso())
                .numero(ubicacion.getNumero())
                .productos(productosDTO)
                .totalProductos(productosDTO.size())
                .cantidadTotal(cantidadTotal)
                .build();
    }
}