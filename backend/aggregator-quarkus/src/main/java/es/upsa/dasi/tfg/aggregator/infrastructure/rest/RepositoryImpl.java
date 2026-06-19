package es.upsa.dasi.tfg.aggregator.infrastructure.rest;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.aggregator.domain.repository.Repository;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.health.HealthClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.informes.InformesClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.mappers.RepositoryMapper;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.proyectos.ProyectosClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.tareas.TareasClient;
import es.upsa.dasi.tfg.aggregator.infrastructure.rest.usuarios.UsuariosClient;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ApplicationScoped
public class RepositoryImpl implements Repository
{
    @Inject @RestClient HealthClient healthClient;
    @Inject @RestClient ProyectosClient proyectosClient;
    @Inject @RestClient TareasClient tareasClient;
    @Inject @RestClient UsuariosClient usuariosClient;
    @Inject @RestClient InformesClient informesClient;
    @Inject RepositoryMapper mapper;

    @Override
    public Map<String, String> findHealth() {
        return healthClient.health();
    }

    @Override
    public List<ProyectoResponse> findProyectos() throws TfgException {
        return proyectosClient.list();
    }

    @Override
    public Proyecto createProyecto(ProyectoPost request) throws TfgException {
        return mapper.toProyecto(proyectosClient.create(request));
    }

    @Override
    public Optional<Proyecto> findProyectoById(long id) throws TfgException {
        try {
            return Optional.of(mapper.toProyecto(proyectosClient.get(id)));
        }
        catch (NotFoundTfgException notFoundTfgException) {
            return Optional.empty();
        }
    }

    @Override
    public void updateProyecto(long id, ProyectoPut request) throws TfgException {
        proyectosClient.update(id, request);
    }

    @Override
    public void removeProyectoById(long id) throws TfgException {
        proyectosClient.delete(id);
    }

    @Override
    public List<HitoResponse> findHitosByProyecto(long proyectoId) throws TfgException {
        return proyectosClient.hitos(proyectoId);
    }

    @Override
    public List<MiembroResponse> findMiembrosByProyecto(long proyectoId) throws TfgException {
        return proyectosClient.miembros(proyectoId);
    }

    @Override
    public List<TareaResponse> findTareasByProyecto(long proyectoId) throws TfgException {
        return tareasClient.listByProyecto(proyectoId);
    }

    @Override
    public Response syncUsuario(UsuarioSync request) throws TfgException {
        return usuariosClient.sync(request);
    }

    @Override
    public UsuarioPerfilResponse findPerfilUsuario() throws TfgException {
        UsuarioPerfilResponse perfil = usuariosClient.perfil();
        try {
            perfil.setParticipaciones(proyectosClient.participaciones());
        }
        catch (TfgException e) {
            perfil.setParticipaciones(List.of());
        }
        return perfil;
    }

    @Override
    public List<NotificacionResponse> findNotificaciones() throws TfgException {
        return usuariosClient.listNotificaciones();
    }

    @Override
    public InformesResumenResponse findInformesResumen() throws TfgException {
        return informesClient.resumen();
    }

    @Override
    public Map<String, Object> findDashboardResumen() throws TfgException {
        Map<String, Object> resumen = new HashMap<>();
        resumen.put("proyectos", findProyectos());
        resumen.put("informes", findInformesResumen());
        return resumen;
    }
}
