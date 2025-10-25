package com.fotomar.inventarioservice.controller;

import com.fotomar.inventarioservice.dto.*;
import com.fotomar.inventarioservice.service.InventarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
@RequiredArgsConstructor
public class InventarioController {
    
    private final InventarioService inventarioService;
    
    @GetMapping("/progreso")
    public ResponseEntity<ProgresoResponse> getProgreso() {
        return ResponseEntity.ok(inventarioService.getProgreso());
    }
    
    @PostMapping("/conteo")
    @PreAuthorize("hasAnyAuthority('OPERADOR', 'SUPERVISOR', 'JEFE')")
    public ResponseEntity<ConteoResponse> registrarConteo(
            @Valid @RequestBody ConteoRequest request,
            @RequestAttribute("userId") Integer userId) {
        return ResponseEntity.ok(inventarioService.registrarConteo(request, userId));
    }
    
    @GetMapping("/diferencias")
    public ResponseEntity<List<DiferenciaResponse>> getDiferencias(
            @RequestParam(required = false) Boolean soloConDiferencias) {
        return ResponseEntity.ok(inventarioService.getDiferencias(soloConDiferencias));
    }
    
    @PostMapping("/finalizar")
    @PreAuthorize("hasAnyAuthority('JEFE', 'SUPERVISOR')")
    public ResponseEntity<FinalizarInventarioResponse> finalizarInventario() {
        return ResponseEntity.ok(inventarioService.finalizarInventario());
    }
}