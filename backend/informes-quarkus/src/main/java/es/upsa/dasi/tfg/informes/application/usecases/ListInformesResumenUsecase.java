package es.upsa.dasi.tfg.informes.application.usecases;

import es.upsa.dasi.tfg.informes.domain.model.InformeResumen;

public interface ListInformesResumenUsecase
{
    InformeResumen execute(String usuarioUid);
}
