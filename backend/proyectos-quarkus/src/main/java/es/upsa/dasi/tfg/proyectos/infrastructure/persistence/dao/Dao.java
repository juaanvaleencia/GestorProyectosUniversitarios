package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.AsignaturaRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaHitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaTareaRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.hito.HitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.InvitacionProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.MiembroRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ParticipacionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.GrupoSupervisionMemberRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.UsuarioRow;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface Dao
{
    List<ProyectoRow> selectProyectosByUsuario(String usuarioUid);
    Optional<ProyectoRow> selectProyectoById(long id);
    ProyectoRow insertProyecto(ProyectoRow row);
    Optional<ProyectoRow> updateProyecto(ProyectoRow row);
    int deleteProyectoById(long id);
    List<HitoRow> selectHitosByProyecto(long proyectoId);
    Optional<HitoRow> selectHitoById(long proyectoId, long hitoId);
    HitoRow insertHito(HitoRow row);
    Optional<HitoRow> updateHito(HitoRow row);
    int deleteHitoById(long proyectoId, long hitoId);
    List<ParticipacionRow> selectParticipacionesByUsuario(String usuarioUid);
    List<ParticipacionRow> selectParticipacionesAlumnoForProfesor(String profesorUid, String alumnoUid);
    List<MiembroRow> selectMiembrosByProyecto(long proyectoId);
    Optional<MiembroRow> selectMiembroById(long proyectoId, long miembroId);
    Optional<MiembroRow> selectMiembroByUsuarioUid(long proyectoId, String usuarioUid);
    Optional<UsuarioRow> selectUsuarioByEmail(String email);
    Optional<UsuarioRow> selectUsuarioByUid(String usuarioUid);
    boolean existsMiembro(long proyectoId, String usuarioUid);
    boolean isProductOwner(long proyectoId, String usuarioUid);
    boolean isMiembroOfProyecto(long proyectoId, String usuarioUid);
    MiembroRow insertMiembro(long proyectoId, String usuarioUid, String rol);
    int deleteMiembroById(long proyectoId, long miembroId);
    void insertNotificacion(String usuarioUid, String texto);
    void insertNotificacion(String usuarioUid, String texto, String tipo, Long invitacionId, Long proyectoId);

    Optional<Long> selectUniversidadIdByUsuarioUid(String usuarioUid);
    boolean isProfesor(String usuarioUid);
    List<AsignaturaRow> selectAsignaturasByUniversidadId(long universidadId);
    List<AsignaturaRow> selectAsignaturasDisponiblesParaProfesor(long universidadId, String profesorUid);
    List<AsignaturaRow> selectAsignaturasMatriculadasByUsuario(String usuarioUid, long universidadId);
    Optional<Long> selectUniversidadIdByAsignaturaId(long asignaturaId);
    Optional<Long> selectAsignaturaIdByPlantillaId(long plantillaId);
    List<PlantillaProyectoRow> selectPlantillasByAsignaturaId(long asignaturaId);
    Optional<PlantillaProyectoRow> selectPlantillaById(long plantillaId);
    List<PlantillaTareaRow> selectTareasByPlantillaId(long plantillaId);
    List<PlantillaHitoRow> selectHitosByPlantillaId(long plantillaId);
    boolean existsProyectoByPropietarioAndPlantilla(String propietarioUid, long plantillaId);
    boolean existsMiembroEnProyectoDePlantilla(String usuarioUid, long plantillaId);
    boolean existsMiembroEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId);
    boolean existsPropietarioEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId);
    Optional<Long> selectPlantillaIdByProyectoId(long proyectoId);
    Optional<String> selectTutorDemoUidByPlantillaId(long plantillaId);
    void insertTareaInstancia(long proyectoId, String titulo, String descripcion, int orden, LocalDate fechaLimite);

    boolean isProfesorDeAsignatura(String usuarioUid, long asignaturaId);
    boolean isProfesorSupervisorOfProyecto(String usuarioUid, long proyectoId);
    List<ProyectoSupervisionRow> selectProyectosByAsignaturaId(long asignaturaId);
    List<GrupoSupervisionMemberRow> selectGruposByPlantillaId(long plantillaId);
    int selectMaxOrdenPlantillaByAsignatura(long asignaturaId);
    long insertPlantillaProyecto(long asignaturaId, String titulo, String descripcion, int orden,
                                 LocalDate fechaInicio, LocalDate fechaFin);
    void insertPlantillaTarea(long plantillaId, String titulo, String descripcion, int orden, LocalDate fechaLimite);
    void insertPlantillaHito(long plantillaId, String titulo, LocalDate fecha, int orden);
    void updatePlantillaProyecto(long plantillaId, String titulo, String descripcion, int orden,
                                 LocalDate fechaInicio, LocalDate fechaFin);
    void deletePlantillaTareasByPlantilla(long plantillaId);
    void deletePlantillaHitosByPlantilla(long plantillaId);

    InvitacionProyectoRow insertInvitacionProyecto(long proyectoId, String usuarioUid, String rol, String invitadoPorUid);
    List<InvitacionProyectoRow> selectInvitacionesPendientesByProyecto(long proyectoId);
    Optional<InvitacionProyectoRow> selectInvitacionById(long invitacionId);
    boolean updateInvitacionEstado(long invitacionId, String estado);
    boolean existsInvitacionPendiente(long proyectoId, String usuarioUid);
    boolean isUsuarioMatriculadoEnAsignatura(String usuarioUid, long asignaturaId);
    Optional<Long> selectAsignaturaIdByProyectoId(long proyectoId);
}
