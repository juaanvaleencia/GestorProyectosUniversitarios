package es.upsa.dasi.tfg.aggregator.infrastructure.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoFullResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.ProyectoResponse;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface RepositoryMapper
{
    @Mapping(target = "fechaInicio", expression = "java(parseDate(response.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(parseDate(response.getFechaFin()))")
    @Mapping(target = "creadoEn", expression = "java(parseDateTime(response.getCreadoEn()))")
    @Mapping(target = "actualizadoEn", expression = "java(parseDateTime(response.getActualizadoEn()))")
    Proyecto toProyecto(ProyectoResponse response);

    @Mapping(target = "fechaInicio", expression = "java(parseDate(response.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(parseDate(response.getFechaFin()))")
    @Mapping(target = "creadoEn", expression = "java(parseDateTime(response.getCreadoEn()))")
    @Mapping(target = "actualizadoEn", expression = "java(parseDateTime(response.getActualizadoEn()))")
    Proyecto toProyecto(ProyectoFullResponse response);

    default LocalDate parseDate(String value)
    {
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    default LocalDateTime parseDateTime(String value)
    {
        return value == null || value.isBlank() ? null : LocalDateTime.parse(value);
    }
}
