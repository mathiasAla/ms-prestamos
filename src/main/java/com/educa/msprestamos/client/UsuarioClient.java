package com.educa.msprestamos.client;

import com.educa.msprestamos.dto.UsuarioDTO;
import com.educa.msprestamos.exception.RecursoNoEncontradoException;
import com.educa.msprestamos.exception.ServicioNoDisponibleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class UsuarioClient {

    private final RestClient restClient;

    public UsuarioClient(@Value("${usuarios.service.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public UsuarioDTO obtenerUsuario(Long id) {
        try {
            return restClient.get()
                    .uri("/api/usuarios/{id}", id)
                    .retrieve()
                    .body(UsuarioDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id " + id);
        } catch (ResourceAccessException e) {
            throw new ServicioNoDisponibleException("El microservicio ms-usuarios no esta disponible");
        }
    }
}