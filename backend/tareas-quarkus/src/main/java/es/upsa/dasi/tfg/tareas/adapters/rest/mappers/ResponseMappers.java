package es.upsa.dasi.tfg.tareas.adapters.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaFullResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.TareaResponse;
import es.upsa.dasi.tfg.tareas.adapters.rest.dtos.TareaPostRequest;
import es.upsa.dasi.tfg.tareas.adapters.rest.dtos.TareaPutRequest;
import es.upsa.dasi.tfg.tareas.domain.model.AddTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.ReplaceTareaCommand;
import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import jakarta.ws.rs.core.UriInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public abstract class ResponseMappers
{
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE;

    @Mapping(target = "fechaLimite", expression = "java(formatDate(t.getFechaLimite()))")
    public abstract TareaResponse toResponse(Tarea t);

    @Mapping(target = "fechaLimite", expression = "java(formatDate(t.getFechaLimite()))")
    @Mapping(target = "uri", expression = "java(createTareaURI(t, uriInfo))")
    public abstract TareaFullResponse toTareaFullResponse(Tarea t, UriInfo uriInfo);

    public abstract AddTareaCommand toAddTareaCommand(TareaPostRequest request);

    public abstract ReplaceTareaCommand toReplaceTareaCommand(TareaPutRequest request);

    public URI createTareaURI(Tarea tarea, UriInfo uriInfo)
    {
        return uriInfo.getBaseUriBuilder()
                .path("/api/proyectos/{proyectoId}/tareas/{id}")
                .resolveTemplate("proyectoId", tarea.getProyectoId())
                .resolveTemplate("id", tarea.getId())
                .build();
    }

    protected String formatDate(LocalDate d)
    {
        return d == null ? null : d.format(ISO);
    }
}
