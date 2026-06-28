package es.upsa.dasi.tfg.proyectos.domain.model.hito;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AddHitoCommand
{
    private String titulo;
    private LocalDate fecha;
    private boolean completado;
}
