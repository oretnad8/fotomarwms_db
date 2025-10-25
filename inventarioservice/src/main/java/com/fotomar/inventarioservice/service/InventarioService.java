package com.fotomar.inventarioservice.service;

import com.fotomar.inventarioservice.dto.*;
import com.fotomar.inventarioservice.exception.ProductoNotFoundException;
import com.fotomar.inventarioservice.exception.UbicacionNotFoundException;
import com.fotomar.inventarioservice.model.*;
import com.fotomar.inventarioservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventarioService {
    
    private final InventarioDiferenciaRepository diferenciaRepository;
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final ProductoUbicacionRepository productoUbicacionRepository;
    private final UsuarioRepository usuarioRepository;
    
    public ProgresoResponse getProgreso() {
        Long totalUbicaciones = ubicacionRepository.count();
        Long ubicacionesContadas = diferenciaRepository.countUbicacionesContadas();
        Long ubicacionesPendientes = totalUbicaciones - ubicacionesContadas;
        Double porcentaje = totalUbicaciones > 0 ? (ubicacionesContadas * 100.0) / totalUbicaciones : 0.0;
        
        Long totalDiferencias = diferenciaRepository.count();
        Long totalFaltantes = diferenciaRepository.countFaltantes();
        Long totalSobrantes = diferenciaRepository.countSobrantes();
        Long ubicacionesConDiferencias = diferenciaRepository.countUbicacionesConDiferencias();
        
        return ProgresoResponse.builder()
                .totalUbicaciones(totalUbicaciones)
                .ubicacionesContadas(ubicacionesContadas)
                .ubicacionesPendientes(ubicacionesPendientes)
                .porcentajeCompletado(Math.round(porcentaje * 100.0) / 100.0)
                .totalDiferenciasRegistradas(totalDiferencias)
                .totalFaltantes(totalFaltantes)
                .totalSobrantes(totalSobrantes)
                .ubicacionesConDiferencias(ubicacionesConDiferencias)
                .build();
    }
    
    @Transactional
    public ConteoResponse registrarConteo(ConteoRequest request, Integer registradorId) {
        String sku = request.getSku().toUpperCase();
        
        // Verificar que el producto existe
        Producto producto = productoRepository.findById(sku)
                .orElseThrow(() -> new ProductoNotFoundException("Producto no encontrado: " + sku));
        
        // Verificar que la ubicación existe
        Ubicacion ubicacion = ubicacionRepository.findById(request.getIdUbicacion())
                .orElseThrow(() -> new UbicacionNotFoundException("Ubicación no encontrada con ID: " + request.getIdUbicacion()));
        
        // Obtener el usuario registrador
        Usuario registrador = usuarioRepository.findById(registradorId)
                .orElseThrow(() -> new RuntimeException("Usuario registrador no encontrado"));
        
        // Obtener la cantidad del sistema (si existe en esa ubicación)
        ProductoUbicacion productoUbicacion = productoUbicacionRepository
                .findByProductoSkuAndUbicacionIdUbicacion(sku, request.getIdUbicacion())
                .orElse(null);
        
        Integer cantidadSistema = productoUbicacion != null ? productoUbicacion.getCantidadEnUbicacion() : 0;
        Integer cantidadFisica = request.getCantidadFisica();
        Integer diferencia = cantidadFisica - cantidadSistema;
        
        // Registrar la diferencia
        InventarioDiferencia inventarioDiferencia = new InventarioDiferencia();
        inventarioDiferencia.setSku(sku);
        inventarioDiferencia.setIdUbicacion(request.getIdUbicacion());
        inventarioDiferencia.setCantidadSistema(cantidadSistema);
        inventarioDiferencia.setCantidadFisica(cantidadFisica);
        inventarioDiferencia.setDiferencia(diferencia);
        inventarioDiferencia.setRegistrador(registrador);
        
        diferenciaRepository.save(inventarioDiferencia);
        
        String tipoAlerta;
        if (diferencia > 0) {
            tipoAlerta = "SOBRANTE";
        } else if (diferencia < 0) {
            tipoAlerta = "FALTANTE";
        } else {
            tipoAlerta = "CORRECTO";
        }
        
        log.info("Conteo registrado - SKU: {}, Ubicación: {}, Sistema: {}, Física: {}, Diferencia: {}", 
                sku, ubicacion.getCodigoUbicacion(), cantidadSistema, cantidadFisica, diferencia);
        
        return ConteoResponse.builder()
                .mensaje("Conteo registrado exitosamente")
                .sku(sku)
                .codigoUbicacion(ubicacion.getCodigoUbicacion())
                .cantidadSistema(cantidadSistema)
                .cantidadFisica(cantidadFisica)
                .diferencia(diferencia)
                .hayDiferencia(diferencia != 0)
                .tipoAlerta(tipoAlerta)
                .build();
    }
    
    public List<DiferenciaResponse> getDiferencias(Boolean soloConDiferencias) {
        List<InventarioDiferencia> diferencias;
        
        if (soloConDiferencias != null && soloConDiferencias) {
            diferencias = diferenciaRepository.findByDiferenciaNot(0);
        } else {
            diferencias = diferenciaRepository.findAllByOrderByFechaRegistroDesc();
        }
        
        return diferencias.stream()
                .map(diferencia -> {
                    Producto producto = productoRepository.findById(diferencia.getSku()).orElse(null);
                    Ubicacion ubicacion = ubicacionRepository.findById(diferencia.getIdUbicacion()).orElse(null);
                    
                    String descripcion = producto != null ? producto.getDescripcion() : "Producto no encontrado";
                    String codigoUbicacion = ubicacion != null ? ubicacion.getCodigoUbicacion() : "Ubicación no encontrada";
                    
                    return DiferenciaResponse.fromEntity(diferencia, descripcion, codigoUbicacion);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public FinalizarInventarioResponse finalizarInventario() {
        List<InventarioDiferencia> diferencias = diferenciaRepository.findByDiferenciaNot(0);
        
        int productosAjustados = 0;
        
        for (InventarioDiferencia diferencia : diferencias) {
            // Buscar el producto-ubicación
            ProductoUbicacion productoUbicacion = productoUbicacionRepository
                    .findByProductoSkuAndUbicacionIdUbicacion(diferencia.getSku(), diferencia.getIdUbicacion())
                    .orElse(null);
            
            if (productoUbicacion != null) {
                // Ajustar la cantidad en ubicación
                productoUbicacion.setCantidadEnUbicacion(diferencia.getCantidadFisica());
                productoUbicacionRepository.save(productoUbicacion);
                productosAjustados++;
            } else if (diferencia.getCantidadFisica() > 0) {
                // Crear nueva asignación si no existe y hay stock físico
                Producto producto = productoRepository.findById(diferencia.getSku()).orElse(null);
                Ubicacion ubicacion = ubicacionRepository.findById(diferencia.getIdUbicacion()).orElse(null);
                
                if (producto != null && ubicacion != null) {
                    ProductoUbicacion nuevaAsignacion = new ProductoUbicacion();
                    nuevaAsignacion.setProducto(producto);
                    nuevaAsignacion.setUbicacion(ubicacion);
                    nuevaAsignacion.setCantidadEnUbicacion(diferencia.getCantidadFisica());
                    productoUbicacionRepository.save(nuevaAsignacion);
                    productosAjustados++;
                }
            }
        }
        
        // Actualizar stock total de productos
        List<Producto> productos = productoRepository.findAll();
        for (Producto producto : productos) {
            Integer stockTotal = productoUbicacionRepository
                    .findAll()
                    .stream()
                    .filter(pu -> pu.getProducto().getSku().equals(producto.getSku()))
                    .mapToInt(ProductoUbicacion::getCantidadEnUbicacion)
                    .sum();
            
            producto.setStock(stockTotal);
            productoRepository.save(producto);
        }
        
        Long totalDiferencias = diferenciaRepository.count();
        Long totalFaltantes = diferenciaRepository.countFaltantes();
        Long totalSobrantes = diferenciaRepository.countSobrantes();
        
        log.info("Inventario finalizado - Total ajustes: {}, Faltantes: {}, Sobrantes: {}", 
                productosAjustados, totalFaltantes, totalSobrantes);
        
        return FinalizarInventarioResponse.builder()
                .mensaje("Inventario finalizado exitosamente")
                .fechaFinalizacion(LocalDateTime.now())
                .totalDiferencias(totalDiferencias)
                .totalFaltantes(totalFaltantes)
                .totalSobrantes(totalSobrantes)
                .productosAjustados(productosAjustados)
                .estado("COMPLETADO")
                .build();
    }
}