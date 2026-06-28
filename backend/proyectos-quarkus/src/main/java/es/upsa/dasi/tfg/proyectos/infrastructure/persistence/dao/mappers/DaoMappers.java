package es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.mappers;

import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.Asignatura;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaHito;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.catalogo.PlantillaTarea;
import es.upsa.dasi.tfg.proyectos.domain.model.hito.Hito;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.AsignaturaRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaHitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.catalogo.PlantillaTareaRow;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.InvitacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.Miembro;
import es.upsa.dasi.tfg.proyectos.domain.model.proyecto.ParticipacionProyecto;
import es.upsa.dasi.tfg.proyectos.domain.model.miembro.UsuarioRegistrado;
import es.upsa.dasi.tfg.common.domain.model.Proyecto;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.hito.HitoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.InvitacionProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.MiembroRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ParticipacionRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.proyecto.ProyectoRow;
import es.upsa.dasi.tfg.proyectos.infrastructure.persistence.dao.dtos.miembro.UsuarioRow;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.CDI)
public interface DaoMappers
{
    Proyecto toProyecto(ProyectoRow row);
    ProyectoRow toProyectoRow(Proyecto proyecto);
    Hito toHito(HitoRow row);
    HitoRow toHitoRow(Hito hito);
    Miembro toMiembro(MiembroRow row);
    InvitacionProyecto toInvitacionProyecto(InvitacionProyectoRow row);
    ParticipacionProyecto toParticipacion(ParticipacionRow row);
    UsuarioRegistrado toUsuarioRegistrado(UsuarioRow row);

    Asignatura toAsignatura(AsignaturaRow row);
    PlantillaProyecto toPlantillaProyecto(PlantillaProyectoRow row);
    PlantillaTarea toPlantillaTarea(PlantillaTareaRow row);
    PlantillaHito toPlantillaHito(PlantillaHitoRow row);
}
