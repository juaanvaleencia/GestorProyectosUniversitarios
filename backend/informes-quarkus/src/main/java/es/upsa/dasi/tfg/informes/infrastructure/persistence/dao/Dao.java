package es.upsa.dasi.tfg.informes.infrastructure.persistence.dao;

import es.upsa.dasi.tfg.informes.infrastructure.persistence.dao.dtos.InformeResumenRow;

public interface Dao
{
    InformeResumenRow selectResumenByUsuario(String usuarioUid);
}
