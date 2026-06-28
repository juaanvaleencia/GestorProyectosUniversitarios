package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.Asignatura;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaHito;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaTarea;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ParticipacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.UsuarioRegistrado;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.GrupoSupervisionMemberRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.mappers.DaoMappers;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RepositoryImpl implements Repository
{
    private final Dao dao;
    private final DaoMappers mappers;

    @Inject
    public RepositoryImpl(Dao dao, DaoMappers mappers) {
        this.dao = dao;
        this.mappers = mappers;
    }

    @Override
    public List<Proyecto> findAllForUser(String usuarioUid) {
        return dao.selectProyectosByUsuario(usuarioUid).stream().map(mappers::toProyecto).toList();
    }

    @Override
    public Proyecto add(Proyecto proyecto) {
        return mappers.toProyecto(dao.insertProyecto(mappers.toProyectoRow(proyecto)));
    }

    @Override
    public Optional<Proyecto> findById(long id) {
        return dao.selectProyectoById(id).map(mappers::toProyecto);
    }

    @Override
    public Proyecto update(Proyecto proyecto) throws NotFoundTfgException {
        return dao.updateProyecto(mappers.toProyectoRow(proyecto))
                .map(mappers::toProyecto)
                .orElseThrow(() -> new NotFoundTfgException("Proyecto no encontrado: " + proyecto.getId()));
    }

    @Override
    public void deleteById(long id) throws NotFoundTfgException {
        if (dao.deleteProyectoById(id) == 0) {
            throw new NotFoundTfgException("Proyecto no encontrado: " + id);
        }
    }

    @Override
    public List<Hito> findHitosByProyecto(long proyectoId) {
        return dao.selectHitosByProyecto(proyectoId).stream().map(mappers::toHito).toList();
    }

    @Override
    public Optional<Hito> findHitoById(long proyectoId, long hitoId) {
        return dao.selectHitoById(proyectoId, hitoId).map(mappers::toHito);
    }

    @Override
    public Hito addHito(Hito hito) {
        return mappers.toHito(dao.insertHito(mappers.toHitoRow(hito)));
    }

    @Override
    public Hito updateHito(Hito hito) throws NotFoundTfgException {
        return dao.updateHito(mappers.toHitoRow(hito))
                .map(mappers::toHito)
                .orElseThrow(() -> new NotFoundTfgException("Hito no encontrado: " + hito.getId()));
    }

    @Override
    public void removeHitoById(long proyectoId, long hitoId) throws NotFoundTfgException {
        if (dao.deleteHitoById(proyectoId, hitoId) == 0) {
            throw new NotFoundTfgException("Hito no encontrado: " + hitoId);
        }
    }

    @Override
    public List<Miembro> findMiembrosByProyecto(long proyectoId) {
        return dao.selectMiembrosByProyecto(proyectoId).stream().map(mappers::toMiembro).toList();
    }

    @Override
    public Optional<Miembro> findMiembroById(long proyectoId, long miembroId) {
        return dao.selectMiembroById(proyectoId, miembroId).map(mappers::toMiembro);
    }

    @Override
    public Optional<Miembro> findMiembroByUsuarioUid(long proyectoId, String usuarioUid) {
        return dao.selectMiembroByUsuarioUid(proyectoId, usuarioUid).map(mappers::toMiembro);
    }

    @Override
    public List<ParticipacionProyecto> findParticipacionesByUsuario(String usuarioUid) {
        return dao.selectParticipacionesByUsuario(usuarioUid).stream().map(mappers::toParticipacion).toList();
    }

    @Override
    public List<ParticipacionProyecto> findParticipacionesAlumnoForProfesor(String profesorUid, String alumnoUid) {
        return dao.selectParticipacionesAlumnoForProfesor(profesorUid, alumnoUid).stream()
                .map(mappers::toParticipacion).toList();
    }

    @Override
    public Optional<UsuarioRegistrado> findUsuarioByEmail(String email) {
        return dao.selectUsuarioByEmail(email).map(mappers::toUsuarioRegistrado);
    }

    @Override
    public Optional<UsuarioRegistrado> findUsuarioByUid(String usuarioUid) {
        return dao.selectUsuarioByUid(usuarioUid).map(mappers::toUsuarioRegistrado);
    }

    @Override
    public Miembro addMiembro(long proyectoId, String usuarioUid, String rol) {
        return mappers.toMiembro(dao.insertMiembro(proyectoId, usuarioUid, rol));
    }

    @Override
    public void removeMiembroById(long proyectoId, long miembroId) throws NotFoundTfgException {
        if (dao.deleteMiembroById(proyectoId, miembroId) == 0) {
            throw new NotFoundTfgException("Miembro no encontrado: " + miembroId);
        }
    }

    @Override
    public void addNotificacion(String usuarioUid, String texto) {
        dao.insertNotificacion(usuarioUid, texto);
    }

    @Override
    public void addNotificacion(String usuarioUid, String texto, String tipo, Long invitacionId, Long proyectoId) {
        dao.insertNotificacion(usuarioUid, texto, tipo, invitacionId, proyectoId);
    }

    @Override
    public boolean isMember(long proyectoId, String usuarioUid) {
        return dao.existsMiembro(proyectoId, usuarioUid);
    }

    @Override
    public boolean isProductOwner(long proyectoId, String usuarioUid) {
        return dao.isProductOwner(proyectoId, usuarioUid);
    }

    @Override
    public boolean isMiembroOfProyecto(long proyectoId, String usuarioUid) {
        return dao.isMiembroOfProyecto(proyectoId, usuarioUid);
    }

    @Override
    public boolean existsProyecto(long id) {
        return dao.selectProyectoById(id).isPresent();
    }

    @Override
    public Optional<Long> findUniversidadIdByUsuarioUid(String usuarioUid) {
        return dao.selectUniversidadIdByUsuarioUid(usuarioUid);
    }

    @Override
    public List<Asignatura> findAsignaturasByUniversidadId(long universidadId) {
        return dao.selectAsignaturasByUniversidadId(universidadId).stream().map(mappers::toAsignatura).toList();
    }

    @Override
    public List<Asignatura> findAsignaturasDisponiblesParaProfesor(long universidadId, String profesorUid) {
        return dao.selectAsignaturasDisponiblesParaProfesor(universidadId, profesorUid).stream()
                .map(mappers::toAsignatura).toList();
    }

    @Override
    public boolean isProfesor(String usuarioUid) {
        return dao.isProfesor(usuarioUid);
    }

    @Override
    public List<Asignatura> findAsignaturasMatriculadasByUsuario(String usuarioUid, long universidadId) {
        return dao.selectAsignaturasMatriculadasByUsuario(usuarioUid, universidadId).stream()
                .map(mappers::toAsignatura).toList();
    }

    @Override
    public Optional<Long> findUniversidadIdByAsignaturaId(long asignaturaId) {
        return dao.selectUniversidadIdByAsignaturaId(asignaturaId);
    }

    @Override
    public Optional<Long> findAsignaturaIdByPlantillaId(long plantillaId) {
        return dao.selectAsignaturaIdByPlantillaId(plantillaId);
    }

    @Override
    public List<PlantillaProyecto> findPlantillasByAsignaturaId(long asignaturaId) {
        return dao.selectPlantillasByAsignaturaId(asignaturaId).stream().map(mappers::toPlantillaProyecto).toList();
    }

    @Override
    public Optional<PlantillaProyecto> findPlantillaDetalleById(long plantillaId) {
        return dao.selectPlantillaById(plantillaId).map(row -> {
            PlantillaProyecto plantilla = mappers.toPlantillaProyecto(row);
            List<PlantillaTarea> tareas = dao.selectTareasByPlantillaId(plantillaId).stream()
                    .map(mappers::toPlantillaTarea).toList();
            List<PlantillaHito> hitos = dao.selectHitosByPlantillaId(plantillaId).stream()
                    .map(mappers::toPlantillaHito).toList();
            plantilla.setTareas(tareas);
            plantilla.setHitos(hitos);
            return plantilla;
        });
    }

    @Override
    public Proyecto createFromPlantilla(PlantillaProyecto plantilla, String propietarioUid) {
        ProyectoRow row = ProyectoRow.builder()
                .titulo(plantilla.getTitulo())
                .descripcion(plantilla.getDescripcion())
                .fechaInicio(plantilla.getFechaInicioSugerida())
                .fechaFin(plantilla.getFechaFinSugerida())
                .estado("PLANIFICACION")
                .propietarioUid(propietarioUid)
                .plantillaId(plantilla.getId())
                .asignaturaId(plantilla.getAsignaturaId())
                .build();
        return mappers.toProyecto(dao.insertProyecto(row));
    }

    @Override
    public boolean existsProyectoByPropietarioAndPlantilla(String propietarioUid, long plantillaId) {
        return dao.existsProyectoByPropietarioAndPlantilla(propietarioUid, plantillaId);
    }

    @Override
    public boolean existsMiembroEnProyectoDePlantilla(String usuarioUid, long plantillaId) {
        return dao.existsMiembroEnProyectoDePlantilla(usuarioUid, plantillaId);
    }

    @Override
    public boolean existsMiembroEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId) {
        return dao.existsMiembroEnOtroProyectoDePlantilla(usuarioUid, plantillaId, excludeProyectoId);
    }

    @Override
    public boolean existsPropietarioEnOtroProyectoDePlantilla(String usuarioUid, long plantillaId, long excludeProyectoId) {
        return dao.existsPropietarioEnOtroProyectoDePlantilla(usuarioUid, plantillaId, excludeProyectoId);
    }

    @Override
    public Optional<Long> findPlantillaIdByProyectoId(long proyectoId) {
        return dao.selectPlantillaIdByProyectoId(proyectoId);
    }

    @Override
    public Optional<String> findTutorDemoUidByPlantillaId(long plantillaId) {
        return dao.selectTutorDemoUidByPlantillaId(plantillaId);
    }

    @Override
    public void addTareaFromPlantilla(long proyectoId, String titulo, String descripcion, int orden, java.time.LocalDate fechaLimite) {
        dao.insertTareaInstancia(proyectoId, titulo, descripcion, orden, fechaLimite);
    }

    @Override
    public boolean isProfesorDeAsignatura(String usuarioUid, long asignaturaId) {
        return dao.isProfesorDeAsignatura(usuarioUid, asignaturaId);
    }

    @Override
    public boolean isProfesorSupervisorOfProyecto(String usuarioUid, long proyectoId) {
        return dao.isProfesorSupervisorOfProyecto(usuarioUid, proyectoId);
    }

    @Override
    public List<es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow> findProyectosSupervisionByAsignaturaId(long asignaturaId) {
        return dao.selectProyectosByAsignaturaId(asignaturaId);
    }

    @Override
    public List<GrupoSupervisionMemberRow> findGruposByPlantillaId(long plantillaId) {
        return dao.selectGruposByPlantillaId(plantillaId);
    }

    @Override
    public int maxOrdenPlantillaByAsignatura(long asignaturaId) {
        return dao.selectMaxOrdenPlantillaByAsignatura(asignaturaId);
    }

    @Override
    public long createPlantillaProyecto(long asignaturaId, String titulo, String descripcion, int orden,
                                        java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        return dao.insertPlantillaProyecto(asignaturaId, titulo, descripcion, orden, fechaInicio, fechaFin);
    }

    @Override
    public void addPlantillaTarea(long plantillaId, String titulo, String descripcion, int orden, java.time.LocalDate fechaLimite) {
        dao.insertPlantillaTarea(plantillaId, titulo, descripcion, orden, fechaLimite);
    }

    @Override
    public void addPlantillaHito(long plantillaId, String titulo, java.time.LocalDate fecha, int orden) {
        dao.insertPlantillaHito(plantillaId, titulo, fecha, orden);
    }

    @Override
    public void updatePlantillaProyecto(long plantillaId, String titulo, String descripcion, int orden,
                                        java.time.LocalDate fechaInicio, java.time.LocalDate fechaFin) {
        dao.updatePlantillaProyecto(plantillaId, titulo, descripcion, orden, fechaInicio, fechaFin);
    }

    @Override
    public void clearPlantillaTareas(long plantillaId) {
        dao.deletePlantillaTareasByPlantilla(plantillaId);
    }

    @Override
    public void clearPlantillaHitos(long plantillaId) {
        dao.deletePlantillaHitosByPlantilla(plantillaId);
    }

    @Override
    public InvitacionProyecto addInvitacionProyecto(long proyectoId, String usuarioUid, String rol, String invitadoPorUid) {
        return mappers.toInvitacionProyecto(dao.insertInvitacionProyecto(proyectoId, usuarioUid, rol, invitadoPorUid));
    }

    @Override
    public List<InvitacionProyecto> findInvitacionesPendientesByProyecto(long proyectoId) {
        return dao.selectInvitacionesPendientesByProyecto(proyectoId).stream()
                .map(mappers::toInvitacionProyecto).toList();
    }

    @Override
    public Optional<InvitacionProyecto> findInvitacionById(long invitacionId) {
        return dao.selectInvitacionById(invitacionId).map(mappers::toInvitacionProyecto);
    }

    @Override
    public boolean updateInvitacionEstado(long invitacionId, String estado) {
        return dao.updateInvitacionEstado(invitacionId, estado);
    }

    @Override
    public boolean existsInvitacionPendiente(long proyectoId, String usuarioUid) {
        return dao.existsInvitacionPendiente(proyectoId, usuarioUid);
    }

    @Override
    public boolean isUsuarioMatriculadoEnAsignatura(String usuarioUid, long asignaturaId) {
        return dao.isUsuarioMatriculadoEnAsignatura(usuarioUid, asignaturaId);
    }

    @Override
    public Optional<Long> findAsignaturaIdByProyectoId(long proyectoId) {
        return dao.selectAsignaturaIdByProyectoId(proyectoId);
    }
}
