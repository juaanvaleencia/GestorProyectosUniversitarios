package es.upsa.dasi.tfg.informes.adapters.rest.mappers;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ActividadDiaResponse;
import es.upsa.dasi.tfg.common.adapters.rest.dtos.InformesResumenResponse;
import es.upsa.dasi.tfg.informes.domain.model.ActividadDia;
import es.upsa.dasi.tfg.informes.domain.model.InformeResumen;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface ResponseMappers
{
    ActividadDiaResponse toActividadDiaResponse(ActividadDia actividadDia);

    InformesResumenResponse toInformesResumenResponse(InformeResumen informeResumen);
}
