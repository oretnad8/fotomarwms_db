package com.fotomar.authservice.service;

import com.fotomar.authservice.dto.LoginRequest;
import com.fotomar.authservice.dto.LoginResponse;
import com.fotomar.authservice.dto.ValidateTokenResponse;
import com.fotomar.authservice.exception.InvalidCredentialsException;
import com.fotomar.authservice.model.Usuario;
import com.fotomar.authservice.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));
        
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        
        String token = jwtService.generateToken(
                usuario.getId(), 
                usuario.getEmail(), 
                usuario.getRol().name()
        );
        
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol())
                .build();
    }
    
    public ValidateTokenResponse validateToken(String token) {
        try {
            if (jwtService.validateToken(token)) {
                Integer userId = jwtService.extractUserId(token);
                String email = jwtService.extractEmail(token);
                String rol = jwtService.extractRol(token);
                
                return ValidateTokenResponse.builder()
                        .valid(true)
                        .userId(userId)
                        .email(email)
                        .rol(Usuario.Rol.valueOf(rol))
                        .message("Token válido")
                        .build();
            }
        } catch (Exception e) {
            return ValidateTokenResponse.builder()
                    .valid(false)
                    .message("Token inválido: " + e.getMessage())
                    .build();
        }
        
        return ValidateTokenResponse.builder()
                .valid(false)
                .message("Token expirado o inválido")
                .build();
    }
    
    public void logout(String token) {
        // En una implementación real, agregarías el token a una lista negra
        // Para este caso básico, el logout se maneja en el cliente eliminando el token
    }
}