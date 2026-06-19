package es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.mappers;

import es.upsa.dasi.tfg.tareas.domain.model.Tarea;
import es.upsa.dasi.tfg.tareas.infrastructure.persistence.dao.dtos.TareaRow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DaoMappers
{
    Tarea toTarea(TareaRow row);
    TareaRow toTareaRow(Tarea tarea);
}
