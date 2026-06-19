package es.upsa.dasi.tfg.proyectos.application.usecases.mappers;

import es.upsa.dasi.tfg.proyectos.domain.model.AddProyectoCommand;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.ReplaceProyectoCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface UsecaseMapper
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "propietarioUid", source = "propietarioUid")
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "actualizadoEn", ignore = true)
    Proyecto toProyecto(AddProyectoCommand command, String propietarioUid);

    @Mapping(target = "id", source = "id")
    @Mapping(target = ".", source = "command")
    @Mapping(target = "propietarioUid", source = "propietarioUid")
    @Mapping(target = "creadoEn", source = "creadoEn")
    @Mapping(target = "actualizadoEn", source = "actualizadoEn")
    Proyecto toProyecto(
            long id,
            ReplaceProyectoCommand command,
            String propietarioUid,
            LocalDateTime creadoEn,
            LocalDateTime actualizadoEn);
}
