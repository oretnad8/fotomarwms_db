package com.fotomar.aprobacionesservice.service;

import com.fotomar.aprobacionesservice.client.UbicacionesClient;
import com.fotomar.aprobacionesservice.dto.AprobacionRequest;
import com.fotomar.aprobacionesservice.dto.AprobacionResponse;
import com.fotomar.aprobacionesservice.dto.AprobarRequest;
import com.fotomar.aprobacionesservice.dto.RechazarRequest;
import com.fotomar.aprobacionesservice.exception.AccionNoPermitidaException;
import com.fotomar.aprobacionesservice.exception.AprobacionNotFoundException;
import com.fotomar.aprobacionesservice.model.Aprobacion;
import com.fotomar.aprobacionesservice.model.Usuario;
import com.fotomar.aprobacionesservice.repository.AprobacionRepository;
import com.fotomar.aprobacionesservice.repository.UbicacionMappingRepository;
import com.fotomar.aprobacionesservice.repository.UsuarioRepository;
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
public class AprobacionService {
    
    private final AprobacionRepository aprobacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UbicacionesClient ubicacionesClient;
    private final UbicacionMappingRepository ubicacionMappingRepository;
    
    public List<AprobacionResponse> getAllAprobaciones(Aprobacion.Estado estado) {
        List<Aprobacion> aprobaciones;
        
        if (estado != null) {
            aprobaciones = aprobacionRepository.findByEstadoOrderByFechaSolicitudDesc(estado);
        } else {
            aprobaciones = aprobacionRepository.findAllByOrderByFechaSolicitudDesc();
        }
        
        return aprobaciones.stream()
                .map(AprobacionResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public AprobacionResponse getAprobacionById(Integer id) {
        Aprobacion aprobacion = aprobacionRepository.findById(id)
                .orElseThrow(() -> new AprobacionNotFoundException("Aprobación no encontrada con ID: " + id));
        return AprobacionResponse.fromEntity(aprobacion);
    }
    
    @Transactional
    public AprobacionResponse createAprobacion(AprobacionRequest request, Integer solicitanteId) {
        Usuario solicitante = usuarioRepository.findById(solicitanteId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Validar que REUBICACION tenga origen y destino
        if (request.getTipoMovimiento() == Aprobacion.TipoMovimiento.REUBICACION) {
            if (request.getIdUbicacionOrigen() == null || request.getIdUbicacionDestino() == null) {
                throw new IllegalArgumentException("La reubicación requiere ubicación de origen y destino");
            }
        }
        
        Aprobacion aprobacion = new Aprobacion();
        aprobacion.setTipoMovimiento(request.getTipoMovimiento());
        aprobacion.setSku(request.getSku().toUpperCase());
        aprobacion.setCantidad(request.getCantidad());
        aprobacion.setMotivo(request.getMotivo());
        aprobacion.setSolicitante(solicitante);
        aprobacion.setEstado(Aprobacion.Estado.PENDIENTE);
        aprobacion.setIdUbicacionOrigen(request.getIdUbicacionOrigen());
        aprobacion.setIdUbicacionDestino(request.getIdUbicacionDestino());
        
        Aprobacion savedAprobacion = aprobacionRepository.save(aprobacion);
        
        log.info("Aprobación creada - ID: {}, Tipo: {}, Solicitante: {}", 
                savedAprobacion.getId(), 
                savedAprobacion.getTipoMovimiento(),
                solicitante.getNombre());
        
        return AprobacionResponse.fromEntity(savedAprobacion);
    }
    
    @Transactional
    public AprobacionResponse aprobarSolicitud(Integer id, AprobarRequest request, Integer aprobadorId) {
        Aprobacion aprobacion = aprobacionRepository.findById(id)
                .orElseThrow(() -> new AprobacionNotFoundException("Aprobación no encontrada con ID: " + id));
        
        Usuario aprobador = usuarioRepository.findById(aprobadorId)
                .orElseThrow(() -> new RuntimeException("Usuario aprobador no encontrado"));
        
        // Validar que no esté ya aprobada o rechazada
        if (aprobacion.getEstado() != Aprobacion.Estado.PENDIENTE) {
            throw new AccionNoPermitidaException("Solo se pueden aprobar solicitudes pendientes");
        }
        
        // Validar que el aprobador tenga permisos (JEFE o SUPERVISOR)
        if (aprobador.getRol() != Usuario.Rol.JEFE && aprobador.getRol() != Usuario.Rol.SUPERVISOR) {
            throw new AccionNoPermitidaException("Solo JEFE o SUPERVISOR pueden aprobar solicitudes");
        }
        
        // Validar que no se apruebe su propia solicitud
        if (aprobacion.getSolicitante().getId().equals(aprobadorId)) {
            throw new AccionNoPermitidaException("No puedes aprobar tu propia solicitud");
        }
        
        aprobacion.setEstado(Aprobacion.Estado.APROBADO);
        aprobacion.setAprobador(aprobador);
        aprobacion.setFechaAprobacion(LocalDateTime.now());
        aprobacion.setObservaciones(request.getObservaciones());
        
        Aprobacion updatedAprobacion = aprobacionRepository.save(aprobacion);
        
        // Ejecutar el movimiento en el servicio de ubicaciones
        try {
            ejecutarMovimiento(updatedAprobacion);
        } catch (Exception e) {
            log.error("Error al ejecutar movimiento para aprobación {}: {}", id, e.getMessage());
            // No revertir la aprobación, pero registrar el error
            // En producción, esto debería manejarse con un sistema de reintentos
        }
        
        log.info("Aprobación {} aprobada por {} ({})", 
                id, aprobador.getNombre(), aprobador.getRol());
        
        // Si el aprobador es SUPERVISOR, debería notificar al JEFE
        if (aprobador.getRol() == Usuario.Rol.SUPERVISOR) {
            log.info("SUPERVISOR aprobó solicitud - Se debe notificar al JEFE");
            // Aquí se podría integrar con mensajes-service para enviar notificación
        }
        
        return AprobacionResponse.fromEntity(updatedAprobacion);
    }
    
    @Transactional
    public AprobacionResponse rechazarSolicitud(Integer id, RechazarRequest request, Integer aprobadorId) {
        Aprobacion aprobacion = aprobacionRepository.findById(id)
                .orElseThrow(() -> new AprobacionNotFoundException("Aprobación no encontrada con ID: " + id));
        
        Usuario aprobador = usuarioRepository.findById(aprobadorId)
                .orElseThrow(() -> new RuntimeException("Usuario aprobador no encontrado"));
        
        // Validar que no esté ya aprobada o rechazada
        if (aprobacion.getEstado() != Aprobacion.Estado.PENDIENTE) {
            throw new AccionNoPermitidaException("Solo se pueden rechazar solicitudes pendientes");
        }
        
        // Validar que el aprobador tenga permisos (JEFE o SUPERVISOR)
        if (aprobador.getRol() != Usuario.Rol.JEFE && aprobador.getRol() != Usuario.Rol.SUPERVISOR) {
            throw new AccionNoPermitidaException("Solo JEFE o SUPERVISOR pueden rechazar solicitudes");
        }
        
        aprobacion.setEstado(Aprobacion.Estado.RECHAZADO);
        aprobacion.setAprobador(aprobador);
        aprobacion.setFechaAprobacion(LocalDateTime.now());
        aprobacion.setObservaciones(request.getObservaciones());
        
        Aprobacion updatedAprobacion = aprobacionRepository.save(aprobacion);
        
        log.info("Aprobación {} rechazada por {} ({}). Motivo: {}", 
                id, aprobador.getNombre(), aprobador.getRol(), request.getObservaciones());
        
        return AprobacionResponse.fromEntity(updatedAprobacion);
    }
    
    public List<AprobacionResponse> getMisSolicitudes(Integer solicitanteId) {
        List<Aprobacion> aprobaciones = aprobacionRepository.findBySolicitanteIdOrderByFechaSolicitudDesc(solicitanteId);
        return aprobaciones.stream()
                .map(AprobacionResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    private void ejecutarMovimiento(Aprobacion aprobacion) {
        String sku = aprobacion.getSku();
        Integer cantidad = aprobacion.getCantidad();
        
        switch (aprobacion.getTipoMovimiento()) {
            case INGRESO:
                if (aprobacion.getIdUbicacionDestino() == null) {
                    throw new IllegalStateException("INGRESO requiere ubicación destino");
                }
                String codigoDestino = obtenerCodigoUbicacion(aprobacion.getIdUbicacionDestino());
                ubicacionesClient.registrarIngreso(sku, codigoDestino, cantidad);
                log.info("Movimiento INGRESO ejecutado: {} unidades de {} en {}", cantidad, sku, codigoDestino);
                break;
                
            case EGRESO:
                if (aprobacion.getIdUbicacionOrigen() == null) {
                    throw new IllegalStateException("EGRESO requiere ubicación origen");
                }
                String codigoOrigen = obtenerCodigoUbicacion(aprobacion.getIdUbicacionOrigen());
                ubicacionesClient.registrarEgreso(sku, codigoOrigen, cantidad);
                log.info("Movimiento EGRESO ejecutado: {} unidades de {} desde {}", cantidad, sku, codigoOrigen);
                break;
                
            case REUBICACION:
                if (aprobacion.getIdUbicacionOrigen() == null || aprobacion.getIdUbicacionDestino() == null) {
                    throw new IllegalStateException("REUBICACION requiere ubicación origen y destino");
                }
                String codigoOrigenReub = obtenerCodigoUbicacion(aprobacion.getIdUbicacionOrigen());
                String codigoDestinoReub = obtenerCodigoUbicacion(aprobacion.getIdUbicacionDestino());
                ubicacionesClient.registrarReubicacion(sku, codigoOrigenReub, codigoDestinoReub, cantidad);
                log.info("Movimiento REUBICACION ejecutado: {} unidades de {} de {} a {}", 
                        cantidad, sku, codigoOrigenReub, codigoDestinoReub);
                break;
        }
    }
    
    private String obtenerCodigoUbicacion(Integer idUbicacion) {
        return ubicacionMappingRepository.findById(idUbicacion)
                .map(mapping -> mapping.getCodigoUbicacion())
                .orElseThrow(() -> new RuntimeException(
                        "No se encontró el código de ubicación para ID: " + idUbicacion));
    }
}