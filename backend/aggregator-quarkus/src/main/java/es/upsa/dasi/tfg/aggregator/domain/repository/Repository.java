package es.upsa.dasi.tfg.aggregator.domain.repository;

import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.HitoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.MiembroInvite;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.ProyectoPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPost;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.TareaPut;
import es.upsa.dasi.tfg.aggregator.adapters.rest.dto.UsuarioSync;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgException;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

public interface Repository
{
    HealthResponse findHealth();

    List<ProyectoResponse> findProyectos() throws TfgException;
    Proyecto createProyecto(ProyectoPost request) throws TfgException;
    Proyecto createProyectoDesdePlantilla(long plantillaId) throws TfgException;
    Optional<Proyecto> findProyectoById(long id) throws TfgException;
    void updateProyecto(long id, ProyectoPut request) throws TfgException;
    void removeProyectoById(long id) throws TfgException;
    List<HitoResponse> findHitosByProyecto(long proyectoId) throws TfgException;
    HitoResponse createHito(long proyectoId, HitoPost request) throws TfgException;
    void updateHito(long proyectoId, long hitoId, HitoPut request) throws TfgException;
    void removeHitoById(long proyectoId, long hitoId) throws TfgException;
    List<MiembroResponse> findMiembrosByProyecto(long proyectoId) throws TfgException;
    InvitacionProyectoResponse inviteMiembro(long proyectoId, MiembroInvite request) throws TfgException;
    void removeMiembroById(long proyectoId, long miembroId) throws TfgException;
    void abandonProyecto(long proyectoId) throws TfgException;
    List<InvitacionProyectoResponse> findInvitacionesByProyecto(long proyectoId) throws TfgException;
    void acceptInvitacion(long invitacionId) throws TfgException;
    void rejectInvitacion(long invitacionId) throws TfgException;

    List<TareaResponse> findTareasByProyecto(long proyectoId) throws TfgException;
    TareaFullResponse createTarea(long proyectoId, TareaPost request) throws TfgException;
    Optional<TareaFullResponse> findTareaById(long proyectoId, long id) throws TfgException;
    void updateTarea(long proyectoId, long id, TareaPut request) throws TfgException;
    void removeTareaById(long proyectoId, long id) throws TfgException;

    Response syncUsuario(UsuarioSync request) throws TfgException;
    UsuarioPerfilResponse findPerfilUsuario() throws TfgException;
    List<AsignaturaResponse> findMisAsignaturas() throws TfgException;
    List<AsignaturaResponse> updateMisAsignaturas(ActualizarMatriculasRequest request) throws TfgException;
    Response createAsignaturaProfesor(CreateAsignaturaProfesorRequest request) throws TfgException;
    Response registroProfesor(RegistroProfesorRequest request) throws TfgException;
    List<NotificacionResponse> findNotificaciones() throws TfgException;
    List<UniversidadResponse> findUniversidades() throws TfgException;

    List<AsignaturaResponse> findAsignaturasByUniversidad(long universidadId) throws TfgException;
    List<PlantillaProyectoResponse> findPlantillasByAsignatura(long asignaturaId) throws TfgException;
    PlantillaProyectoDetalleResponse findPlantillaById(long plantillaId) throws TfgException;
    PlantillaProyectoDetalleResponse updatePlantilla(long plantillaId, CreatePlantillaProyectoRequest request)
            throws TfgException;
    PlantillaProyectoDetalleResponse createPlantilla(long asignaturaId, CreatePlantillaProyectoRequest request) throws TfgException;
    List<ProyectoSupervisionResponse> findProyectosSupervisionByAsignatura(long asignaturaId) throws TfgException;
    List<ProyectoGrupoSupervisionResponse> findGruposByPlantilla(long plantillaId) throws TfgException;

    List<AlumnoMatriculadoResponse> findAlumnosMatriculados() throws TfgException;
    AlumnoPerfilSupervisionResponse findAlumnoPerfil(String alumnoUid) throws TfgException;

    InformesResumenResponse findInformesResumen() throws TfgException;

    DashboardResumenResponse findDashboardResumen() throws TfgException;
}
