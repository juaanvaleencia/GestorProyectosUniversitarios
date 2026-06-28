package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.common.domain.model.Universidad;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AlumnoMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.mappers.DaoMappers;
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
    public RepositoryImpl(Dao dao, DaoMappers mappers)
    {
        this.dao = dao;
        this.mappers = mappers;
    }

    @Override
    public Optional<Usuario> findByUid(String firebaseUid)
    {
        return dao.selectUsuarioByUid(firebaseUid)
                  .map(mappers::toUsuario);
    }

    @Override
    public Usuario add(Usuario usuario)
    {
        return mappers.toUsuario(dao.insertUsuario(mappers.toUsuarioRow(usuario)));
    }

    @Override
    public List<Notificacion> findNotificacionesByUsuario(String usuarioUid) {
        return dao.selectNotificacionesByUsuario(usuarioUid).stream().map(mappers::toNotificacion).toList();
    }

    @Override
    public void addNotificacion(String usuarioUid, String texto) {
        dao.insertNotificacion(usuarioUid, texto);
    }

    @Override
    public List<Universidad> findAllUniversidades() {
        return dao.selectAllUniversidades().stream().map(mappers::toUniversidad).toList();
    }

    @Override
    public Optional<Universidad> findUniversidadById(long universidadId) {
        return dao.selectUniversidadById(universidadId).map(mappers::toUniversidad);
    }

    @Override
    public boolean existsUniversidad(long universidadId) {
        return dao.existsUniversidad(universidadId);
    }

    @Override
    public Optional<String> findCodigoProfesorByUniversidadId(long universidadId) {
        return dao.selectCodigoProfesorByUniversidadId(universidadId);
    }

    @Override
    public List<AsignaturaMatriculaRow> findAsignaturasMatriculadasByUsuario(String usuarioUid) {
        return dao.selectAsignaturasMatriculadasByUsuario(usuarioUid);
    }

    @Override
    public List<AsignaturaMatriculaRow> findAsignaturasImpartidasByProfesor(String usuarioUid) {
        return dao.selectAsignaturasImpartidasByProfesor(usuarioUid);
    }

    @Override
    public void replaceAsignaturasMatriculadas(String usuarioUid, List<Long> asignaturaIds) {
        dao.replaceAsignaturasMatriculadas(usuarioUid, asignaturaIds);
    }

    @Override
    public void replaceProfesorAsignaturas(String usuarioUid, List<Long> asignaturaIds) {
        dao.replaceProfesorAsignaturas(usuarioUid, asignaturaIds);
    }

    @Override
    public void updateAsignaturaTutor(long asignaturaId, String tutorUid, String tutorNombre) {
        dao.updateAsignaturaTutor(asignaturaId, tutorUid, tutorNombre);
    }

    @Override
    public void clearAsignaturaTutor(long asignaturaId) {
        dao.clearAsignaturaTutor(asignaturaId);
    }

    @Override
    public boolean asignaturasPertenecenAUniversidad(List<Long> asignaturaIds, long universidadId) {
        return dao.asignaturasPertenecenAUniversidad(asignaturaIds, universidadId);
    }

    @Override
    public List<String> findNombresAsignaturasOcupadasPorOtrosProfesores(List<Long> asignaturaIds, String profesorUid) {
        return dao.findNombresAsignaturasOcupadasPorOtrosProfesores(asignaturaIds, profesorUid);
    }

    @Override
    public List<AlumnoMatriculaRow> findAlumnosMatriculadosByProfesor(String profesorUid) {
        return dao.selectAlumnosMatriculadosByProfesor(profesorUid);
    }

    @Override
    public boolean profesorSupervisaAlumno(String profesorUid, String alumnoUid) {
        return dao.profesorSupervisaAlumno(profesorUid, alumnoUid);
    }

    @Override
    public AsignaturaMatriculaRow createAsignaturaParaProfesor(
            String usuarioUid, long universidadId, String nombre, String descripcion, String tutorNombre)
    {
        return dao.createAsignaturaParaProfesor(usuarioUid, universidadId, nombre, descripcion, tutorNombre);
    }
}
