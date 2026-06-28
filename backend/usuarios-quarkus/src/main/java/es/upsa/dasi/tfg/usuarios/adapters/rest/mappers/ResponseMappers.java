package es.upsa.dasi.tfg.usuarios.adapters.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.AsignaturaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.NotificacionResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UniversidadResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.UsuarioPerfilResponse;
import es.upsa.dasi.tfg.common.domain.model.TipoUsuario;
import es.upsa.dasi.tfg.usuarios.adapters.rest.dtos.UsuarioSyncRequest;
import es.upsa.dasi.tfg.usuarios.domain.model.Notificacion;
import es.upsa.dasi.tfg.usuarios.domain.model.SyncUsuarioCommand;
import es.upsa.dasi.tfg.common.domain.model.Universidad;
import es.upsa.dasi.tfg.common.domain.model.Usuario;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import es.upsa.dasi.tfg.usuarios.infrastructure.persistence.dao.dtos.AsignaturaMatriculaRow;
import jakarta.inject.Inject;
import java.util.List;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class ResponseMappers
{
    @Inject
    Repository repository;

    @Mapping(target = "universidadNombre", ignore = true)
    @Mapping(target = "participaciones", ignore = true)
    @Mapping(target = "tipo", ignore = true)
    @Mapping(target = "matriculacionCompleta", ignore = true)
    @Mapping(target = "asignaturasMatriculadas", ignore = true)
    @Mapping(target = "asignaturasImpartidas", ignore = true)
    public abstract UsuarioPerfilResponse toUsuarioPerfilResponse(Usuario usuario);

    @AfterMapping
    protected void enrichPerfil(@MappingTarget UsuarioPerfilResponse target, Usuario usuario)
    {
        enrichUniversidadNombre(target, usuario);
        TipoUsuario tipo = usuario.getTipo() != null ? usuario.getTipo() : TipoUsuario.ESTUDIANTE;
        target.setTipo(tipo.name());
        var matriculas = repository.findAsignaturasMatriculadasByUsuario(usuario.getFirebaseUid()).stream()
                .map(this::toAsignaturaResponse)
                .toList();
        target.setAsignaturasMatriculadas(matriculas);
        if (tipo == TipoUsuario.PROFESOR) {
            var impartidas = repository.findAsignaturasImpartidasByProfesor(usuario.getFirebaseUid()).stream()
                    .map(this::toAsignaturaResponse)
                    .toList();
            target.setAsignaturasImpartidas(impartidas);
            target.setMatriculacionCompleta(!impartidas.isEmpty());
        } else {
            target.setAsignaturasImpartidas(List.of());
            target.setMatriculacionCompleta(!matriculas.isEmpty());
        }
    }

    @AfterMapping
    protected void enrichUniversidadNombre(@MappingTarget UsuarioPerfilResponse target, Usuario usuario)
    {
        if (usuario.getUniversidadId() == null) {
            return;
        }
        repository.findUniversidadById(usuario.getUniversidadId())
                .map(Universidad::getNombre)
                .ifPresent(target::setUniversidadNombre);
    }

    private AsignaturaResponse toAsignaturaResponse(AsignaturaMatriculaRow row)
    {
        return AsignaturaResponse.builder()
                .id(row.getId())
                .universidadId(row.getUniversidadId())
                .nombre(row.getNombre())
                .descripcion(row.getDescripcion())
                .tutorNombre(row.getTutorNombre())
                .build();
    }

    public abstract UniversidadResponse toUniversidadResponse(Universidad universidad);

    public abstract SyncUsuarioCommand toSyncUsuarioCommand(UsuarioSyncRequest request);

    @Mapping(target = "creadoEn", expression = "java(notificacion.getCreadoEn() != null ? notificacion.getCreadoEn().toString() : null)")
    public abstract NotificacionResponse toNotificacionResponse(Notificacion notificacion);
}
