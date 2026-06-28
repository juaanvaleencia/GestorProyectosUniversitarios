package es.upsa.dasi.tfg.proyectos.application.catalogo;

import es.upsa.dasi.tfg.common.domain.exceptions.ForbiddenTfgException;
import es.upsa.dasi.tfg.common.domain.exceptions.NotFoundTfgException;
import es.upsa.dasi.tfg.proyectos.application.proyecto.ProyectoAuthorizationService;
import es.upsa.dasi.tfg.proyectos.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CatalogoAuthorizationService
{
    @Inject ProyectoAuthorizationService authz;
    @Inject Repository repository;

    public void requireUniversidadDelUsuario(long universidadId)
    {
        Long userUniversidadId = repository.findUniversidadIdByUsuarioUid(authz.currentUid())
                .orElseThrow(() -> new ForbiddenTfgException("Debes indicar tu universidad en el perfil"));
        if (!userUniversidadId.equals(universidadId)) {
            throw new ForbiddenTfgException("No tienes acceso al catálogo de esa universidad");
        }
    }

    public void requireAsignaturaDelUsuario(long asignaturaId) throws NotFoundTfgException
    {
        long universidadId = repository.findUniversidadIdByAsignaturaId(asignaturaId)
                .orElseThrow(() -> new NotFoundTfgException("Asignatura no encontrada: " + asignaturaId));
        requireUniversidadDelUsuario(universidadId);
    }

    public void requirePlantillaDelUsuario(long plantillaId) throws NotFoundTfgException
    {
        long asignaturaId = repository.findAsignaturaIdByPlantillaId(plantillaId)
                .orElseThrow(() -> new NotFoundTfgException("Plantilla no encontrada: " + plantillaId));
        requireAsignaturaAccesible(asignaturaId);
    }

    public void requireAsignaturaAccesible(long asignaturaId) throws NotFoundTfgException
    {
        String uid = authz.currentUid();
        if (repository.isProfesorDeAsignatura(uid, asignaturaId)) {
            return;
        }
        requireAsignaturaDelUsuario(asignaturaId);
    }

    public void requireProfesorImparteAsignatura(long asignaturaId) throws NotFoundTfgException
    {
        repository.findUniversidadIdByAsignaturaId(asignaturaId)
                .orElseThrow(() -> new NotFoundTfgException("Asignatura no encontrada: " + asignaturaId));
        authz.requireProfesorDeAsignatura(asignaturaId);
    }
}
