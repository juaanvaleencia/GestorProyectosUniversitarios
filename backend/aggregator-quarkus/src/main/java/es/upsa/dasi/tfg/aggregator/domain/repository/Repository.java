package es.upsa.dasi.tfg.aggregator.domain.repository;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface Repository
{
    Map<String, String> findHealth();

    List<ProyectoResponse> findProyectos() throws TfgException;
    Proyecto createProyecto(ProyectoPost request) throws TfgException;
    Optional<Proyecto> findProyectoById(long id) throws TfgException;
    void updateProyecto(long id, ProyectoPut request) throws TfgException;
    void removeProyectoById(long id) throws TfgException;
    List<HitoResponse> findHitosByProyecto(long proyectoId) throws TfgException;
    List<MiembroResponse> findMiembrosByProyecto(long proyectoId) throws TfgException;

    List<TareaResponse> findTareasByProyecto(long proyectoId) throws TfgException;

    Response syncUsuario(UsuarioSync request) throws TfgException;
    UsuarioPerfilResponse findPerfilUsuario() throws TfgException;
    List<NotificacionResponse> findNotificaciones() throws TfgException;

    InformesResumenResponse findInformesResumen() throws TfgException;

    Map<String, Object> findDashboardResumen() throws TfgException;
}
