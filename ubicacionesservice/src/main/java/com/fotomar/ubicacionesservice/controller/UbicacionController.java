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
            @RequestParam(required = false) Character piso,
            @RequestParam(required = false) Integer pasillo) {
        
        // Filtrar por pasillo y piso
        if (pasillo != null && piso != null) {
            return ResponseEntity.ok(ubicacionService.getUbicacionesByPasilloPiso(pasillo, piso));
        }
        
        // Filtrar solo por pasillo
        if (pasillo != null) {
            return ResponseEntity.ok(ubicacionService.getUbicacionesByPasillo(pasillo));
        }
        
        // Filtrar solo por piso
        if (piso != null) {
            return ResponseEntity.ok(ubicacionService.getUbicacionesByPiso(piso));
        }
        
        // Todas las ubicaciones
        return ResponseEntity.ok(ubicacionService.getAllUbicaciones());
    }
    
    @GetMapping("/{codigo}")
    public ResponseEntity<UbicacionResponse> getUbicacionByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(ubicacionService.getUbicacionByCodigo(codigo));
    }
    
    @GetMapping("/pasillo/{pasillo}/posicion/{numero}")
    public ResponseEntity<List<UbicacionResponse>> getUbicacionesByPasilloNumero(
            @PathVariable Integer pasillo,
            @PathVariable Integer numero) {
        return ResponseEntity.ok(ubicacionService.getUbicacionesByPasilloNumero(pasillo, numero));
    }
    
    @PostMapping("/asignar")
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR', 'OPERADOR')")
    public ResponseEntity<AsignarProductoResponse> asignarProducto(
            @Valid @RequestBody AsignarProductoRequest request) {
        return ResponseEntity.ok(ubicacionService.asignarProducto(request));
    }
}