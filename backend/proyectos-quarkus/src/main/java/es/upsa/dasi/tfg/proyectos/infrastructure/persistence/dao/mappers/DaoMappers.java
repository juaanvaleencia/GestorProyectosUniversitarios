package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.mappers;

import es.upsa.dasi.tfg.proyectos.domain.model.Hito;
import es.upsa.dasi.tfg.proyectos.domain.model.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.ParticipacionProyecto;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.HitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.MiembroRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.ParticipacionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.ProyectoRow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DaoMappers
{
    Proyecto toProyecto(ProyectoRow row);
    ProyectoRow toProyectoRow(Proyecto proyecto);
    Hito toHito(HitoRow row);
    Miembro toMiembro(MiembroRow row);
    ParticipacionProyecto toParticipacion(ParticipacionRow row);
}
