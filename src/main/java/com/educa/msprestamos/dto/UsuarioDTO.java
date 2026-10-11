package com.educa.msprestamos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record UsuarioDTO(Long id, String nombre, String correo, String rol) {
}