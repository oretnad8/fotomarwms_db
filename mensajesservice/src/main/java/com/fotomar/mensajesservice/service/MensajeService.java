package com.fotomar.mensajesservice.service;

import com.fotomar.mensajesservice.dto.MensajeRequest;
import com.fotomar.mensajesservice.dto.MensajeResponse;
import com.fotomar.mensajesservice.dto.MensajeResumenDTO;
import com.fotomar.mensajesservice.exception.MensajeNotFoundException;
import com.fotomar.mensajesservice.exception.UsuarioNotFoundException;
import com.fotomar.mensajesservice.model.Mensaje;
import com.fotomar.mensajesservice.model.Usuario;
import com.fotomar.mensajesservice.repository.MensajeRepository;
import com.fotomar.mensajesservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MensajeService {
    
    private final MensajeRepository mensajeRepository;
    private final UsuarioRepository usuarioRepository;
    
    public List<MensajeResponse> getMensajesParaUsuario(Integer userId, Boolean soloNoLeidos, Boolean soloImportantes) {
        List<Mensaje> mensajes;
        
        if (soloNoLeidos != null && soloNoLeidos) {
            mensajes = mensajeRepository.findMensajesNoLeidosParaUsuario(userId);
        } else if (soloImportantes != null && soloImportantes) {
            mensajes = mensajeRepository.findMensajesImportantesParaUsuario(userId);
        } else {
            mensajes = mensajeRepository.findMensajesParaUsuario(userId);
        }
        
        return mensajes.stream()
                .map(MensajeResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public MensajeResumenDTO getResumenMensajes(Integer userId) {
        Long total = mensajeRepository.countMensajesParaUsuario(userId);
        Long noLeidos = mensajeRepository.countMensajesNoLeidosParaUsuario(userId);
        Long importantes = mensajeRepository.countMensajesImportantesParaUsuario(userId);
        Long importantesNoLeidos = mensajeRepository.countMensajesImportantesNoLeidosParaUsuario(userId);
        
        return MensajeResumenDTO.builder()
                .totalMensajes(total)
                .mensajesNoLeidos(noLeidos)
                .mensajesImportantes(importantes)
                .mensajesImportantesNoLeidos(importantesNoLeidos)
                .build();
    }
    
    public List<MensajeResponse> getMensajesEnviados(Integer emisorId) {
        List<Mensaje> mensajes = mensajeRepository.findByEmisorIdOrderByFechaDesc(emisorId);
        return mensajes.stream()
                .map(MensajeResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public MensajeResponse createMensaje(MensajeRequest request, Integer emisorId) {
        Usuario emisor = usuarioRepository.findById(emisorId)
                .orElseThrow(() -> new UsuarioNotFoundException("Usuario emisor no encontrado"));
        
        Mensaje mensaje = new Mensaje();
        mensaje.setEmisor(emisor);
        mensaje.setTitulo(request.getTitulo());
        mensaje.setContenido(request.getContenido());
        mensaje.setImportante(request.getImportante() != null ? request.getImportante() : false);
        mensaje.setLeido(false);
        
        // Si idDestinatario es null, el mensaje es para todos
        if (request.getIdDestinatario() != null) {
            Usuario destinatario = usuarioRepository.findById(request.getIdDestinatario())
                    .orElseThrow(() -> new UsuarioNotFoundException("Usuario destinatario no encontrado"));
            mensaje.setDestinatario(destinatario);
        } else {
            mensaje.setDestinatario(null);
        }
        
        Mensaje savedMensaje = mensajeRepository.save(mensaje);
        
        String tipo = savedMensaje.getDestinatario() != null ? "individual" : "para todos";
        log.info("Mensaje {} creado por {} - Título: {}", tipo, emisor.getNombre(), savedMensaje.getTitulo());
        
        return MensajeResponse.fromEntity(savedMensaje);
    }
    
    @Transactional
    public MensajeResponse marcarComoLeido(Integer mensajeId, Integer userId) {
        Mensaje mensaje = mensajeRepository.findById(mensajeId)
                .orElseThrow(() -> new MensajeNotFoundException("Mensaje no encontrado con ID: " + mensajeId));
        
        // Verificar que el usuario tiene acceso a este mensaje
        boolean tieneAcceso = mensaje.getDestinatario() == null || 
                              mensaje.getDestinatario().getId().equals(userId);
        
        if (!tieneAcceso) {
            throw new RuntimeException("No tienes acceso a este mensaje");
        }
        
        if (!mensaje.getLeido()) {
            mensaje.setLeido(true);
            Mensaje updatedMensaje = mensajeRepository.save(mensaje);
            log.info("Mensaje {} marcado como leído por usuario {}", mensajeId, userId);
            return MensajeResponse.fromEntity(updatedMensaje);
        }
        
        return MensajeResponse.fromEntity(mensaje);
    }
    
    @Transactional
    public MensajeResponse toggleImportante(Integer mensajeId, Integer userId) {
        Mensaje mensaje = mensajeRepository.findById(mensajeId)
                .orElseThrow(() -> new MensajeNotFoundException("Mensaje no encontrado con ID: " + mensajeId));
        
        // Solo el emisor puede marcar/desmarcar como importante
        if (!mensaje.getEmisor().getId().equals(userId)) {
            throw new RuntimeException("Solo el emisor puede modificar la importancia del mensaje");
        }
        
        mensaje.setImportante(!mensaje.getImportante());
        Mensaje updatedMensaje = mensajeRepository.save(mensaje);
        
        log.info("Mensaje {} marcado como {} por usuario {}", 
                mensajeId, 
                updatedMensaje.getImportante() ? "importante" : "normal", 
                userId);
        
        return MensajeResponse.fromEntity(updatedMensaje);
    }
    
    public MensajeResponse getMensajeById(Integer id, Integer userId) {
        Mensaje mensaje = mensajeRepository.findById(id)
                .orElseThrow(() -> new MensajeNotFoundException("Mensaje no encontrado con ID: " + id));
        
        // Verificar que el usuario tiene acceso a este mensaje
        boolean tieneAcceso = mensaje.getDestinatario() == null || 
                              mensaje.getDestinatario().getId().equals(userId) ||
                              mensaje.getEmisor().getId().equals(userId);
        
        if (!tieneAcceso) {
            throw new RuntimeException("No tienes acceso a este mensaje");
        }
        
        return MensajeResponse.fromEntity(mensaje);
    }
}