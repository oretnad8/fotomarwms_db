package com.fotomar.ubicacionesservice.service;

import com.fotomar.ubicacionesservice.dto.AsignarProductoRequest;
import com.fotomar.ubicacionesservice.dto.AsignarProductoResponse;
import com.fotomar.ubicacionesservice.dto.UbicacionResponse;
import com.fotomar.ubicacionesservice.exception.ProductoNotFoundException;
import com.fotomar.ubicacionesservice.exception.StockInsuficienteException;
import com.fotomar.ubicacionesservice.exception.UbicacionNotFoundException;
import com.fotomar.ubicacionesservice.model.Producto;
import com.fotomar.ubicacionesservice.model.ProductoUbicacion;
import com.fotomar.ubicacionesservice.model.Ubicacion;
import com.fotomar.ubicacionesservice.repository.ProductoRepository;
import com.fotomar.ubicacionesservice.repository.ProductoUbicacionRepository;
import com.fotomar.ubicacionesservice.repository.UbicacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UbicacionService {
    
    private final UbicacionRepository ubicacionRepository;
    private final ProductoRepository productoRepository;
    private final ProductoUbicacionRepository productoUbicacionRepository;
    
    public List<UbicacionResponse> getAllUbicaciones() {
        return ubicacionRepository.findAll().stream()
                .map(UbicacionResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<UbicacionResponse> getUbicacionesByPiso(Character piso) {
        if (piso != 'A' && piso != 'B' && piso != 'C') {
            throw new IllegalArgumentException("Piso debe ser A, B o C");
        }
        
        return ubicacionRepository.findByPisoOrderByNumero(piso).stream()
                .map(UbicacionResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public UbicacionResponse getUbicacionByCodigo(String codigo) {
        Ubicacion ubicacion = ubicacionRepository.findByCodigoUbicacion(codigo.toUpperCase())
                .orElseThrow(() -> new UbicacionNotFoundException("Ubicación no encontrada: " + codigo));
        return UbicacionResponse.fromEntity(ubicacion);
    }
    
    @Transactional
    public AsignarProductoResponse asignarProducto(AsignarProductoRequest request) {
        String sku = request.getSku().toUpperCase();
        String codigoUbicacion = request.getCodigoUbicacion().toUpperCase();
        
        // Verificar que el producto existe
        Producto producto = productoRepository.findById(sku)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado: " + sku));
        
        // Verificar que la ubicación existe
        Ubicacion ubicacion = ubicacionRepository.findByCodigoUbicacion(codigoUbicacion)
                .orElseThrow(() -> new UbicacionNotFoundException("Ubicación no encontrada: " + codigoUbicacion));
        
        // Verificar stock disponible
        Integer cantidadActualmenteAsignada = productoUbicacionRepository.sumCantidadByProductoSku(sku);
        if (cantidadActualmenteAsignada == null) {
            cantidadActualmenteAsignada = 0;
        }
        
        Integer stockDisponible = producto.getStock() - cantidadActualmenteAsignada;
        
        if (stockDisponible < request.getCantidad()) {
            throw new StockInsuficienteException(
                    String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                            stockDisponible, request.getCantidad()));
        }
        
        // Buscar si ya existe una asignación en esta ubicación
        ProductoUbicacion productoUbicacion = productoUbicacionRepository
                .findByProductoSkuAndUbicacionIdUbicacion(sku, ubicacion.getIdUbicacion())
                .orElse(null);
        
        if (productoUbicacion != null) {
            // Ya existe, incrementar cantidad
            productoUbicacion.setCantidadEnUbicacion(
                    productoUbicacion.getCantidadEnUbicacion() + request.getCantidad());
        } else {
            // Nueva asignación
            productoUbicacion = new ProductoUbicacion();
            productoUbicacion.setProducto(producto);
            productoUbicacion.setUbicacion(ubicacion);
            productoUbicacion.setCantidadEnUbicacion(request.getCantidad());
        }
        
        productoUbicacionRepository.save(productoUbicacion);
        
        Integer nuevoStockAsignado = productoUbicacionRepository.sumCantidadByProductoSku(sku);
        Integer nuevoStockDisponible = producto.getStock() - nuevoStockAsignado;
        
        log.info("Producto {} asignado a ubicación {}. Cantidad: {}", sku, codigoUbicacion, request.getCantidad());
        
        return AsignarProductoResponse.builder()
                .mensaje("Producto asignado exitosamente")
                .sku(sku)
                .codigoUbicacion(codigoUbicacion)
                .cantidadAsignada(request.getCantidad())
                .stockRestante(nuevoStockDisponible)
                .build();
    }
}