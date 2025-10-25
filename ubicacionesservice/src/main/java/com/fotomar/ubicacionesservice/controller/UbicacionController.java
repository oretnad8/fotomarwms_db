package com.fotomar.ubicacionesservice.controller;

import com.fotomar.ubicacionesservice.dto.AsignarProductoRequest;
import com.fotomar.ubicacionesservice.dto.AsignarProductoResponse;
import com.fotomar.ubicacionesservice.dto.UbicacionResponse;
import com.fotomar.ubicacionesservice.service.UbicacionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ubicaciones")
@RequiredArgsConstructor
public class UbicacionController {
    
    private final UbicacionService ubicacionService;
    
    @GetMapping
    public ResponseEntity<List<UbicacionResponse>> getUbicaciones(
            @RequestParam(required = false) Character piso) {
        
        if (piso != null) {
            return ResponseEntity.ok(ubicacionService.getUbicacionesByPiso(piso));
        }
        
        return ResponseEntity.ok(ubicacionService.getAllUbicaciones());
    }
    
    @GetMapping("/{codigo}")
    public ResponseEntity<UbicacionResponse> getUbicacionByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ubicacionService.getUbicacionByCodigo(codigo));
    }
    
    @PostMapping("/asignar")
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR', 'OPERADOR')")
    public ResponseEntity<AsignarProductoResponse> asignarProducto(
            @Valid @RequestBody AsignarProductoRequest request) {
        return ResponseEntity.ok(ubicacionService.asignarProducto(request));
    }
}