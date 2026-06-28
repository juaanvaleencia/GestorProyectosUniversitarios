package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipanteSupervisionResponse
{
    String uid;
    String nombre;
    String email;
    String rol;
    String rolEtiqueta;
    boolean propietario;
}
