package com.educa.msprestamos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LibroDTO(Long id, String titulo, String autor, Integer stock, CategoriaDTO categoria) {

    public LibroDTO conStock(int nuevoStock) {
        return new LibroDTO(id, titulo, autor, nuevoStock, categoria);
    }
}