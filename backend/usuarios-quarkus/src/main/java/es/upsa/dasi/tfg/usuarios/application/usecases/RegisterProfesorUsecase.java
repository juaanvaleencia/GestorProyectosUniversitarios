package es.upsa.dasi.tfg.usuarios.application.usecases;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.RegistroProfesorRequest;
import es.upsa.dasi.tfg.common.domain.model.Usuario;

public interface RegisterProfesorUsecase
{
    Usuario execute(RegistroProfesorRequest request);
}
