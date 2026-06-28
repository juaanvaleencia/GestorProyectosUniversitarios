package es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AlumnoMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.NotificacionRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UniversidadRow;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.UsuarioRow;

import java.util.List;
import java.util.Optional;

public interface Dao
{
    Optional<UsuarioRow> selectUsuarioByUid(String firebaseUid);
    UsuarioRow insertUsuario(UsuarioRow row);
    List<NotificacionRow> selectNotificacionesByUsuario(String usuarioUid);
    void insertNotificacion(String usuarioUid, String texto);
    List<UniversidadRow> selectAllUniversidades();
    Optional<UniversidadRow> selectUniversidadById(long universidadId);
    boolean existsUniversidad(long universidadId);
    Optional<String> selectCodigoProfesorByUniversidadId(long universidadId);
    List<AsignaturaMatriculaRow> selectAsignaturasMatriculadasByUsuario(String usuarioUid);
    List<AsignaturaMatriculaRow> selectAsignaturasImpartidasByProfesor(String usuarioUid);
    void replaceAsignaturasMatriculadas(String usuarioUid, List<Long> asignaturaIds);
    void replaceProfesorAsignaturas(String usuarioUid, List<Long> asignaturaIds);
    void updateAsignaturaTutor(long asignaturaId, String tutorUid, String tutorNombre);
    void clearAsignaturaTutor(long asignaturaId);
    boolean asignaturasPertenecenAUniversidad(List<Long> asignaturaIds, long universidadId);
    List<String> findNombresAsignaturasOcupadasPorOtrosProfesores(List<Long> asignaturaIds, String profesorUid);
    List<AlumnoMatriculaRow> selectAlumnosMatriculadosByProfesor(String profesorUid);
    boolean profesorSupervisaAlumno(String profesorUid, String alumnoUid);
    AsignaturaMatriculaRow createAsignaturaParaProfesor(String usuarioUid, long universidadId, String nombre, String descripcion, String tutorNombre);
}
