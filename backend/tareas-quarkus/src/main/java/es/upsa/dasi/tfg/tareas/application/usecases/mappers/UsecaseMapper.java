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
    Tarea toTarea(AddTareaCommand command, long proyectoId);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "proyectoId", source = "proyectoId")
    @Mapping(target = "fechaLimite", expression = "java(parseDate(command.getFechaLimite()))")
    @Mapping(target = "creadoEn", source = "creadoEn")
    Tarea toTarea(long id, long proyectoId, ReplaceTareaCommand command, LocalDateTime creadoEn);

    default LocalDate parseDate(String value)
    {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }
}
