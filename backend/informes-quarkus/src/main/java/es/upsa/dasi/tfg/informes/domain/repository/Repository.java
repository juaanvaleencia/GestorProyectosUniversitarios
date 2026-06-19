package es.upsa.dasi.tfg.informes.domain.repository;

import es.upsa.dasi.tfg.informes.domain.model.InformeResumen;

public interface Repository
{
    InformeResumen findResumenByUsuario(String usuarioUid);
}
