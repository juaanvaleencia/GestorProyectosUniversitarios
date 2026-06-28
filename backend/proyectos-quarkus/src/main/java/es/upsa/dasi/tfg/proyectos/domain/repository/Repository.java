package es.upsa.dasi.tfg.proyectos.domain.repository;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.Asignatura;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ParticipacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.UsuarioRegistrado;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;

import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.GrupoSupervisionMemberRow;

import java.util.List;
import java.util.Optional;

public interface Repository
{
    List<Proyecto> findAllForUser(String usuarioUid);
    Proyecto add(Proyecto proyecto);
    Optional<Proyecto> findById(long id);
    Proyecto update(Proyecto proyecto) throws NotFoundTfgException;
    void deleteById(long id) throws NotFoundTfgException;
    List<Hito> findHitosByProyecto(long proyectoId);
    Optional<Hito> findHitoById(long proyectoId, long hitoId);
    Hito addHito(Hito hito);
    Hito updateHito(Hito hito) throws NotFoundTfgException;
    void removeHitoById(long proyectoId, long hitoId) throws NotFoundTfgException;
    List<Miembro> findMiembrosByProyecto(long proyectoId);
    Optional<Miembro> findMiembroById(long proyectoId, long miembroId);
    Optional<Miembro> findMiembroByUsuarioUid(long proyectoId, String usuarioUid);
    List<ParticipacionProyecto> findParticipacionesByUsuario(String usuarioUid);
    List<ParticipacionProyecto> findParticipacionesAlumnoForProfesor(String profesorUid, String alumnoUid);
    Optional<UsuarioRegistrado> findUsuarioByEmail(String email);
    Optional<UsuarioRegistrado> findUsuarioByUid(String usuarioUid);
    Miembro addMiembro(long proyectoId, String usuarioUid, String rol);
    void removeMiembroById(long proyectoId, long miembroId) throws NotFoundTfgException;
    void addNotificacion(String usuarioUid, String texto);
    void addNotificacion(String usuarioUid, String texto, String tipo, Long invitacionId, Long proyectoId);
    boolean isMember(long proyectoId, String usuarioUid);
    boolean isProductOwner(long proyectoId, String usuarioUid);
    boolean isMiembroOfProyecto(long proyectoId, String usuarioUid);
    boolean existsProyecto(long id);

    Optional<Long> findUniversidadIdByUsuarioUid(String usuarioUid);
    boolean isProfesor(String usuarioUid);
    List<Asignatura> findAsignaturasByUniversidadId(long universidadId);
    List<Asignatura> findAsignaturasDisponiblesParaProfesor(long universidadId, String profesorUid);
    List<Asignatura> findAsignaturasMatriculadasByUsuario(String usuarioUid, long universidadId);
    Optional<Long> findUniversidadIdByAsignaturaId(long asignaturaId);
    Optional<Long> findAsignaturaIdByPlantillaId(long plantillaId);
    List<PlantillaProyecto> findPlantillasByAsignaturaId(long asignaturaId);
    Optional<PlantillaProyecto> findPlantillaDetalleById(long plantillaId);
    Proyecto createFromPlantilla(PlantillaProyecto plantilla, String propietarioUid);
    boolean existsProyectoByPropietarioAndPlantilla(String propietarioUid, long plantillaId);
    boolean existsMiembroEnProyectoDePlantilla(String usuarioUid, long plantillaId);
    boolean existsMiembroEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId);
    boolean existsPropietarioEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId);
    Optional<Long> findPlantillaIdByProyectoId(long proyectoId);
    Optional<String> findTutorDemoUidByPlantillaId(long plantillaId);
    void addTareaFromPlantilla(long proyectoId, String titulo, String descripcion, int orden, java.time.LocalDate fechaLimite);

    boolean isProfesorDeAsignatura(String usuarioUid, long asignaturaId);
    boolean isProfesorSupervisorOfProyecto(String usuarioUid, long proyectoId);
    List<es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow> findProyectosSupervisionByAsignaturaId(long asignaturaId);
    List<GrupoSupervisionMemberRow> findGruposByPlantillaId(long plantillaId);
    int maxOrdenPlantillaByAsignatura(long asignaturaId);
    long createPlantillaProyecto(long asignaturaId, String titulo, String descripcion, int orden,
                                 java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);
    void addPlantillaTarea(long plantillaId, String titulo, String descripcion, int orden, java.time.LocalDate fechaLimite);
    void addPlantillaHito(long plantillaId, String titulo, java.time.LocalDate fecha, int orden);
    void updatePlantillaProyecto(long plantillaId, String titulo, String descripcion, int orden,
                                 java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin);
    void clearPlantillaTareas(long plantillaId);
    void clearPlantillaHitos(long plantillaId);

    InvitacionProyecto addInvitacionProyecto(long proyectoId, String usuarioUid, String rol, String invitadoPorUid);
    List<InvitacionProyecto> findInvitacionesPendientesByProyecto(long proyectoId);
    Optional<InvitacionProyecto> findInvitacionById(long invitacionId);
    boolean updateInvitacionEstado(long invitacionId, String estado);
    boolean existsInvitacionPendiente(long proyectoId, String usuarioUid);
    boolean isUsuarioMatriculadoEnAsignatura(String usuarioUid, long asignaturaId);
    Optional<Long> findAsignaturaIdByProyectoId(long proyectoId);
}
