package com.fotomar.authservice.dto;

import com.fotomar.authservice.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String nombre;
    private String email;
    private Usuario.Rol rol;
}