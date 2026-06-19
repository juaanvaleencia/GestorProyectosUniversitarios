package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.mappers;

import es.upsa.dasi.tfg.informes.domain.model.ActividadDia;
import es.upsa.dasi.tfg.informes.domain.model.InformeResumen;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.ActividadDiaRow;
import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.InformeResumenRow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DaoMappers
{
    ActividadDia toActividadDia(ActividadDiaRow row);

    InformeResumen toInformeResumen(InformeResumenRow row);
}
