package com.educa.msprestamos.client;

import com.educa.msprestamos.dto.LibroDTO;
import com.educa.msprestamos.exception.RecursoNoEncontradoException;
import com.educa.msprestamos.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class CatalogoClient {

    private final RestClient restClient;

    public CatalogoClient(@Value("${catalogo.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public LibroDTO obtenerLibro(Long id) {
        try {
            return restClient.get()
                    .uri("/api/libros/{id}", id)
                    .retrieve()
                    .body(LibroDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Libro no encontrado con id " + id);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El microservicio ms-catalogo no esta disponible");
        }
    }

    public void actualizarLibro(LibroDTO libro) {
        try {
            restClient.put()
                    .uri("/api/libros/{id}", libro.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(libro)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El microservicio ms-catalogo no esta disponible");
        }
    }
}