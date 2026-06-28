package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroProfesorRequest
{
    @NotBlank
    private String firebaseUid;
    @NotBlank @Email
    private String email;
    @NotBlank
    private String nombre;
    @NotNull
    private Long universidadId;
    @NotBlank
    private String codigoProfesor;
}
