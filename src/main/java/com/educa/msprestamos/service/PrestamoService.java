package com.educa.msprestamos.service;

import com.educa.msprestamos.client.CatalogoClient;
import com.educa.msprestamos.client.UsuarioClient;
import com.educa.msprestamos.dto.LibroDTO;
import com.educa.msprestamos.dto.PrestamoDetalleDTO;
import com.educa.msprestamos.dto.UsuarioDTO;
import com.educa.msprestamos.exception.ConflictoException;
import com.educa.msprestamos.exception.RecursoNoEncontradoException;
import com.educa.msprestamos.model.EstadoPrestamo;
import com.educa.msprestamos.model.Prestamo;
import com.educa.msprestamos.model.PrestamoRequest;
import com.educa.msprestamos.repository.PrestamoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PrestamoService {

    private static final int DIAS_DE_PRESTAMO = 14;

    private final PrestamoRepository prestamoRepository;
    private final UsuarioClient usuarioClient;
    private final CatalogoClient catalogoClient;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           UsuarioClient usuarioClient,
                           CatalogoClient catalogoClient) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioClient = usuarioClient;
        this.catalogoClient = catalogoClient;
    }

    public List<Prestamo> listar() {
        return prestamoRepository.findAll();
    }

    public Prestamo buscarPorId(Long id) {
        return prestamoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Prestamo no encontrado con id " + id));
    }

    public List<Prestamo> listarPorUsuario(Long usuarioId) {
        usuarioClient.obtenerUsuario(usuarioId);
        return prestamoRepository.findByUsuarioId(usuarioId);
    }

    
    public PrestamoDetalleDTO obtenerDetalle(Long id) {
        Prestamo prestamo = buscarPorId(id);
        UsuarioDTO usuario = usuarioClient.obtenerUsuario(prestamo.getUsuarioId());
        LibroDTO libro = catalogoClient.obtenerLibro(prestamo.getLibroId());
        return new PrestamoDetalleDTO(prestamo, usuario, libro);
    }

    @Transactional
    public Prestamo crear(PrestamoRequest request) {
        
        usuarioClient.obtenerUsuario(request.usuarioId());

        
        LibroDTO libro = catalogoClient.obtenerLibro(request.libroId());
        if (libro.stock() == null || libro.stock() <= 0) {
            throw new ConflictoException("No hay stock disponible del libro '" + libro.titulo() + "'");
        }

        
        catalogoClient.actualizarLibro(libro.conStock(libro.stock() - 1));

        
        Prestamo prestamo = new Prestamo();
        prestamo.setUsuarioId(request.usuarioId());
        prestamo.setLibroId(request.libroId());
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucionEstimada(LocalDate.now().plusDays(DIAS_DE_PRESTAMO));
        prestamo.setEstado(EstadoPrestamo.ACTIVO);
        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public Prestamo registrarDevolucion(Long id) {
        Prestamo prestamo = buscarPorId(id);
        if (prestamo.getEstado() == EstadoPrestamo.DEVUELTO) {
            throw new ConflictoException("El prestamo " + id + " ya fue devuelto");
        }

        
        LibroDTO libro = catalogoClient.obtenerLibro(prestamo.getLibroId());
        catalogoClient.actualizarLibro(libro.conStock(libro.stock() + 1));

        prestamo.setFechaDevolucionReal(LocalDate.now());
        prestamo.setEstado(EstadoPrestamo.DEVUELTO);
        return prestamoRepository.save(prestamo);
    }

    @Transactional
    public void eliminar(Long id) {
        prestamoRepository.delete(buscarPorId(id));
    }
}