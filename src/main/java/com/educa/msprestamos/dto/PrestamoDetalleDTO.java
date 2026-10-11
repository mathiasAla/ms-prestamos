package com.educa.msprestamos.dto;

import com.educa.msprestamos.model.Prestamo;


public record PrestamoDetalleDTO(Prestamo prestamo, UsuarioDTO usuario, LibroDTO libro) {
}