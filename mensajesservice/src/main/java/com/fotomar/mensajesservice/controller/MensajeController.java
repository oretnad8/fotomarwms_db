package com.fotomar.mensajesservice.controller;

import com.fotomar.mensajesservice.dto.MensajeRequest;
import com.fotomar.mensajesservice.dto.MensajeResponse;
import com.fotomar.mensajesservice.dto.MensajeResumenDTO;
import com.fotomar.mensajesservice.service.MensajeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mensajes")
@RequiredArgsConstructor
public class MensajeController {
    
    private final MensajeService mensajeService;
    
    @GetMapping
    public ResponseEntity<List<MensajeResponse>> getMensajes(
            @RequestAttribute("userId") Integer userId,
            @RequestParam(required = false) Boolean soloNoLeidos,
            @RequestParam(required = false) Boolean soloImportantes) {
        return ResponseEntity.ok(mensajeService.getMensajesParaUsuario(userId, soloNoLeidos, soloImportantes));
    }
    
    @GetMapping("/resumen")
    public ResponseEntity<MensajeResumenDTO> getResumenMensajes(@RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(mensajeService.getResumenMensajes(userId));
    }
    
    @GetMapping("/enviados")
    public ResponseEntity<List<MensajeResponse>> getMensajesEnviados(@RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(mensajeService.getMensajesEnviados(userId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponse> getMensajeById(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(mensajeService.getMensajeById(id, userId));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<MensajeResponse> createMensaje(
            @Valid @RequestBody MensajeRequest request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(mensajeService.createMensaje(request, userId));
    }
    
    @PutMapping("/{id}/marcar-leido")
    public ResponseEntity<MensajeResponse> marcarComoLeido(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(mensajeService.marcarComoLeido(id, userId));
    }
    
    @PutMapping("/{id}/toggle-importante")
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR', 'ADMIN')")
    public ResponseEntity<MensajeResponse> toggleImportante(
            @PathVariable Integer id,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(mensajeService.toggleImportante(id, userId));
    }
}