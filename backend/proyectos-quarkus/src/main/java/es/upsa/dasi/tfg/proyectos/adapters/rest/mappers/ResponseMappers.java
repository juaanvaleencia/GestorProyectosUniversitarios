package es.upsa.dasi.tfg.proyectos.adapters.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.adapters.rest.dtos.ProyectoPostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.dtos.ProyectoPutRequest;
import es.upsa.dasi.tfg.proyectos.domain.model.*;
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

    @Mapping(target = "fechaInicio", expression = "java(formatDate(p.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(formatDate(p.getFechaFin()))")
    @Mapping(target = "creadoEn", expression = "java(p.getCreadoEn() != null ? p.getCreadoEn().toString() : null)")
    @Mapping(target = "actualizadoEn", expression = "java(p.getActualizadoEn() != null ? p.getActualizadoEn().toString() : null)")
    public abstract ProyectoResponse toResponse(Proyecto p);

    @Mapping(target = "fechaInicio", expression = "java(formatDate(p.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(formatDate(p.getFechaFin()))")
    @Mapping(target = "creadoEn", expression = "java(p.getCreadoEn() != null ? p.getCreadoEn().toString() : null)")
    @Mapping(target = "actualizadoEn", expression = "java(p.getActualizadoEn() != null ? p.getActualizadoEn().toString() : null)")
    @Mapping(target = "uri", expression = "java(createProyectoURI(p, uriInfo))")
    public abstract ProyectoFullResponse toProyectoFullResponse(Proyecto p, UriInfo uriInfo);

    @Mapping(target = "fecha", expression = "java(formatDate(h.getFecha()))")
    public abstract HitoResponse toResponse(Hito h);

    @Mapping(target = "rolEtiqueta", expression = "java(es.upsa.dasi.tfg.common.domain.model.RolProyecto.etiquetaDe(m.getRol()))")
    public abstract MiembroResponse toResponse(Miembro m);

    @Mapping(target = "fechaInicio", expression = "java(parseDateLocal(request.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(parseDateLocal(request.getFechaFin()))")
    public abstract AddProyectoCommand toAddProyectoCommand(ProyectoPostRequest request);

    @Mapping(target = "fechaInicio", expression = "java(parseDateLocal(request.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(parseDateLocal(request.getFechaFin()))")
    public abstract ReplaceProyectoCommand toReplaceProyectoCommand(ProyectoPutRequest request);

    public URI createProyectoURI(Proyecto proyecto, UriInfo uriInfo)
    {
        return uriInfo.getBaseUriBuilder()
                .path("/api/proyectos/{id}")
                .resolveTemplate("id", proyecto.getId())
                .build();
    }

    protected LocalDate parseDateLocal(String s) {
        return s == null || s.isBlank() ? null : LocalDate.parse(s);
    }

    protected String formatDate(LocalDate d) {
        return d == null ? null : d.format(ISO);
    }
}
