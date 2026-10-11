package com.educa.msprestamos.controller;

import com.educa.msprestamos.dto.PrestamoDetalleDTO;
import com.educa.msprestamos.model.Prestamo;
import com.educa.msprestamos.model.PrestamoRequest;
import com.educa.msprestamos.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public List<Prestamo> listar() {
        return prestamoService.listar();
    }

    @GetMapping("/{id}")
    public Prestamo obtener(@PathVariable Long id) {
        return prestamoService.buscarPorId(id);
    }

    
    @GetMapping("/{id}/detalle")
    public PrestamoDetalleDTO detalle(@PathVariable Long id) {
        return prestamoService.obtenerDetalle(id);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<Prestamo> listarPorUsuario(@PathVariable Long usuarioId) {
        return prestamoService.listarPorUsuario(usuarioId);
    }

    @PostMapping
    public ResponseEntity<Prestamo> crear(@Valid @RequestBody PrestamoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(prestamoService.crear(request));
    }

    @PutMapping("/{id}/devolucion")
    public Prestamo devolver(@PathVariable Long id) {
        return prestamoService.registrarDevolucion(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        prestamoService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}