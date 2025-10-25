package com.fotomar.aprobacionesservice.controller;

import com.fotomar.aprobacionesservice.dto.AprobacionRequest;
import com.fotomar.aprobacionesservice.dto.AprobacionResponse;
import com.fotomar.aprobacionesservice.dto.AprobarRequest;
import com.fotomar.aprobacionesservice.dto.RechazarRequest;
import com.fotomar.aprobacionesservice.model.Aprobacion;
import com.fotomar.aprobacionesservice.service.AprobacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aprobaciones")
@RequiredArgsConstructor
public class AprobacionController {
    
    private final AprobacionService aprobacionService;
    
    @GetMapping
    public ResponseEntity<List<AprobacionResponse>> getAprobaciones(
            @RequestParam(required = false) Aprobacion.Estado estado) {
        return ResponseEntity.ok(aprobacionService.getAllAprobaciones(estado));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AprobacionResponse> getAprobacionById(@PathVariable Integer id) {
        return ResponseEntity.ok(aprobacionService.getAprobacionById(id));
    }
    
    @PostMapping
    @PreAuthorize("hasAnyAuthority('OPERADOR', 'SUPERVISOR', 'JEFE')")
    public ResponseEntity<AprobacionResponse> createAprobacion(
            @Valid @RequestBody AprobacionRequest request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(aprobacionService.createAprobacion(request, userId));
    }
    
    @PutMapping("/{id}/aprobar")
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR')")
    public ResponseEntity<AprobacionResponse> aprobarSolicitud(
            @PathVariable Integer id,
            @Valid @RequestBody AprobarRequest request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(aprobacionService.aprobarSolicitud(id, request, userId));
    }
    
    @PutMapping("/{id}/rechazar")
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR')")
    public ResponseEntity<AprobacionResponse> rechazarSolicitud(
            @PathVariable Integer id,
            @Valid @RequestBody RechazarRequest request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(aprobacionService.rechazarSolicitud(id, request, userId));
    }
    
    @GetMapping("/mis-solicitudes")
    @PreAuthorize("hasAnyAuthority('OPERADOR', 'SUPERVISOR', 'JEFE')")
    public ResponseEntity<List<AprobacionResponse>> getMisSolicitudes(
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(aprobacionService.getMisSolicitudes(userId));
    }
}