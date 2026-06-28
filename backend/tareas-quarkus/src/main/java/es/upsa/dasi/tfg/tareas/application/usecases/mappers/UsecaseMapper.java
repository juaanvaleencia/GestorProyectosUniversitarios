package es.upsa.dasi.tfg.tareas.application.usecases.mappers;

import es.upsa.dasi.tfg.tareas.domain.model.AddTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.ReplaceTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface UsecaseMapper
{
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "proyectoId", source = "proyectoId")
    @Mapping(target = "fechaLimite", expression = "java(parseDate(command.getFechaLimite()))")
    @Mapping(target = "creadoEn", ignore = true)
    @Mapping(target = "origen", constant = "ALUMNO")
    @Mapping(target = "tareaPadreId", source = "command.tareaPadreId")
    @Mapping(target = "letraSubtarea", ignore = true)
    Tarea toTarea(AddTareaCommand command, long proyectoId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "proyectoId", source = "proyectoId")
    @Mapping(target = "titulo", source = "command.titulo")
    @Mapping(target = "descripcion", source = "command.descripcion")
    @Mapping(target = "estado", source = "command.estado")
    @Mapping(target = "prioridad", source = "command.prioridad")
    @Mapping(target = "responsableUid", source = "command.responsableUid")
    @Mapping(target = "orden", source = "command.orden")
    @Mapping(target = "fechaLimite", expression = "java(parseDate(command.getFechaLimite()))")
    @Mapping(target = "creadoEn", source = "creadoEn")
    @Mapping(target = "origen", source = "actual.origen")
    @Mapping(target = "tareaPadreId", source = "actual.tareaPadreId")
    @Mapping(target = "letraSubtarea", source = "actual.letraSubtarea")
    Tarea toTarea(long id, long proyectoId, ReplaceTareaCommand command, LocalDateTime creadoEn, Tarea actual);

    default LocalDate parseDate(String value)
    {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}
