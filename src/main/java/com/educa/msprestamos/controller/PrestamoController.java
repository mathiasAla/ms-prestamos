package com.educa.msprestamos.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    @PostMapping
    public String crearPrestamo(@RequestBody String prestamo) {
        return "Prestamo creado, fecha de devolucion calculada";
    }

    @PutMapping("/{id}/devolucion")
    public String registrarDevolucion(@PathVariable Long id) {
        return "Devolucion registrada para el prestamo " + id;
    }

    @GetMapping("/{id}")
    public String consultarPrestamo(@PathVariable Long id) {
        return "Detalle del prestamo " + id;
    }
}
