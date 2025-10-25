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
public class ValidateTokenResponse {
    private boolean valid;
    private Integer userId;
    private String email;
    private Usuario.Rol rol;
    private String message;
}