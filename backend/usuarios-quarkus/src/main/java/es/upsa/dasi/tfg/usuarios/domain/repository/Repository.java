package es.upsa.dasi.tfg.usuarios.domain.repository;

import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.common.domain.model.Universidad;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AlumnoMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;

import java.util.List;
import java.util.Optional;

public interface Repository
{
    Optional<Usuario> findByUid(String firebaseUid);
    Usuario add(Usuario usuario);
    List<Notificacion> findNotificacionesByUsuario(String usuarioUid);
    void addNotificacion(String usuarioUid, String texto);
    List<Universidad> findAllUniversidades();
    Optional<Universidad> findUniversidadById(long universidadId);
    boolean existsUniversidad(long universidadId);
    Optional<String> findCodigoProfesorByUniversidadId(long universidadId);
    List<AsignaturaMatriculaRow> findAsignaturasMatriculadasByUsuario(String usuarioUid);
    List<AsignaturaMatriculaRow> findAsignaturasImpartidasByProfesor(String usuarioUid);
    void replaceAsignaturasMatriculadas(String usuarioUid, List<Long> asignaturaIds);
    void replaceProfesorAsignaturas(String usuarioUid, List<Long> asignaturaIds);
    void updateAsignaturaTutor(long asignaturaId, String tutorUid, String tutorNombre);
    void clearAsignaturaTutor(long asignaturaId);
    boolean asignaturasPertenecenAUniversidad(List<Long> asignaturaIds, long universidadId);
    List<String> findNombresAsignaturasOcupadasPorOtrosProfesores(List<Long> asignaturaIds, String profesorUid);
    List<AlumnoMatriculaRow> findAlumnosMatriculadosByProfesor(String profesorUid);
    boolean profesorSupervisaAlumno(String profesorUid, String alumnoUid);
    AsignaturaMatriculaRow createAsignaturaParaProfesor(String usuarioUid, long universidadId, String nombre, String descripcion, String tutorNombre);
}
