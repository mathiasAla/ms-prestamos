package com.educa.msprestamos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CategoriaDTO(Long id, String nombre) {
}