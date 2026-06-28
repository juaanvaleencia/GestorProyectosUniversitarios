package es.upsa.dasi.tfg.proyectos.adapters.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.*;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.adapters.rest.proyecto.ProyectoPostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.proyecto.ProyectoPutRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.miembro.MiembroInvitePostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.hito.HitoPostRequest;
import es.upsa.dasi.tfg.proyectos.adapters.rest.hito.HitoPutRequest;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.Asignatura;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaHito;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaTarea;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.AddHitoCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.ReplaceHitoCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InviteMiembroCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.AddProyectoCommand;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ReplaceProyectoCommand;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoSupervisionRow;
import jakarta.ws.rs.core.UriInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    @Mapping(target = "rolEtiqueta", expression = "java(es.upsa.dasi.tfg.common.domain.model.RolProyecto.etiquetaDe(i.getRol()))")
    @Mapping(target = "creadoEn", expression = "java(i.getCreadoEn() != null ? i.getCreadoEn().toString() : null)")
    public abstract InvitacionProyectoResponse toResponse(InvitacionProyecto i);

    @Mapping(target = "fechaInicio", expression = "java(parseDateLocal(request.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(parseDateLocal(request.getFechaFin()))")
    public abstract AddProyectoCommand toAddProyectoCommand(ProyectoPostRequest request);

    @Mapping(target = "fechaInicio", expression = "java(parseDateLocal(request.getFechaInicio()))")
    @Mapping(target = "fechaFin", expression = "java(parseDateLocal(request.getFechaFin()))")
    public abstract ReplaceProyectoCommand toReplaceProyectoCommand(ProyectoPutRequest request);

    public abstract InviteMiembroCommand toInviteMiembroCommand(MiembroInvitePostRequest request);

    @Mapping(target = "fecha", expression = "java(parseDateLocal(request.getFecha()))")
    public abstract AddHitoCommand toAddHitoCommand(HitoPostRequest request);

    @Mapping(target = "fecha", expression = "java(parseDateLocal(request.getFecha()))")
    public abstract ReplaceHitoCommand toReplaceHitoCommand(HitoPutRequest request);

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

    public AsignaturaResponse toAsignaturaResponse(Asignatura a) {
        return AsignaturaResponse.builder()
                .id(a.getId())
                .universidadId(a.getUniversidadId())
                .nombre(a.getNombre())
                .descripcion(a.getDescripcion())
                .tutorNombre(a.getTutorNombre())
                .build();
    }

    public PlantillaProyectoResponse toPlantillaProyectoResponse(PlantillaProyecto p) {
        return PlantillaProyectoResponse.builder()
                .id(p.getId())
                .asignaturaId(p.getAsignaturaId())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .orden(p.getOrden())
                .fechaInicioSugerida(formatDate(p.getFechaInicioSugerida()))
                .fechaFinSugerida(formatDate(p.getFechaFinSugerida()))
                .tutorNombre(p.getTutorNombre())
                .numTareas(p.getNumTareas())
                .numHitos(p.getNumHitos())
                .build();
    }

    public PlantillaProyectoDetalleResponse toPlantillaProyectoDetalleResponse(PlantillaProyecto p) {
        return PlantillaProyectoDetalleResponse.builder()
                .id(p.getId())
                .asignaturaId(p.getAsignaturaId())
                .asignaturaNombre(p.getAsignaturaNombre())
                .titulo(p.getTitulo())
                .descripcion(p.getDescripcion())
                .orden(p.getOrden())
                .fechaInicioSugerida(formatDate(p.getFechaInicioSugerida()))
                .fechaFinSugerida(formatDate(p.getFechaFinSugerida()))
                .tutorNombre(p.getTutorNombre())
                .tareas(p.getTareas() == null ? List.of() : p.getTareas().stream().map(this::toPlantillaTareaResponse).toList())
                .hitos(p.getHitos() == null ? List.of() : p.getHitos().stream().map(this::toPlantillaHitoResponse).toList())
                .build();
    }

    public PlantillaTareaResponse toPlantillaTareaResponse(PlantillaTarea t) {
        return PlantillaTareaResponse.builder()
                .id(t.getId())
                .titulo(t.getTitulo())
                .descripcion(t.getDescripcion())
                .orden(t.getOrden())
                .fechaLimiteSugerida(formatDate(t.getFechaLimiteSugerida()))
                .build();
    }

    public PlantillaHitoResponse toPlantillaHitoResponse(PlantillaHito h) {
        return PlantillaHitoResponse.builder()
                .id(h.getId())
                .titulo(h.getTitulo())
                .fechaSugerida(formatDate(h.getFechaSugerida()))
                .orden(h.getOrden())
                .build();
    }

    public ProyectoSupervisionResponse toProyectoSupervisionResponse(ProyectoSupervisionRow row) {
        return ProyectoSupervisionResponse.builder()
                .id(row.getId())
                .titulo(row.getTitulo())
                .estado(row.getEstado())
                .propietarioNombre(row.getPropietarioNombre())
                .propietarioEmail(row.getPropietarioEmail())
                .fechaInicio(formatDate(row.getFechaInicio()))
                .fechaFin(formatDate(row.getFechaFin()))
                .actualizadoEn(row.getActualizadoEn() != null ? row.getActualizadoEn().toString() : null)
                .build();
    }
}
