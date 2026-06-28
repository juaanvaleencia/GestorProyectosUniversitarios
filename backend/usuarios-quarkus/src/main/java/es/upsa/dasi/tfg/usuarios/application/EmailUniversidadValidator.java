package es.upsa.dasi.tfg.usuarios.application;

import es.upsa.dasi.tfg.common.adapters.rest.dtos.ErrorResponse;
import es.upsa.dasi.tfg.common.domain.exceptions.TfgValidationRuntimeException;
import es.upsa.dasi.tfg.common.domain.model.Universidad;
import es.upsa.dasi.tfg.usuarios.domain.repository.Repository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.Locale;
import java.util.regex.Pattern;

@ApplicationScoped
public class EmailUniversidadValidator
{
    private static final Pattern UPSA = Pattern.compile("^.+@upsa\\.es$", Pattern.CASE_INSENSITIVE);
    private static final Pattern USAL = Pattern.compile("^.+@usal\\.es$", Pattern.CASE_INSENSITIVE);

    @Inject
    Repository repository;

    public void validar(String email, Long universidadId)
    {
        if (universidadId == null) {
            return;
        }
        Universidad universidad = repository.findUniversidadById(universidadId)
                .orElseThrow(() -> new TfgValidationRuntimeException(new ErrorResponse[] {
                        ErrorResponse.builder().status("400").message("La universidad indicada no existe").build()
                }));

        String codigo = universidad.getCodigo() == null ? "" : universidad.getCodigo().toUpperCase(Locale.ROOT);
        String correo = email == null ? "" : email.trim();

        boolean valido = switch (codigo) {
            case "UPSA" -> UPSA.matcher(correo).matches();
            case "USAL" -> USAL.matcher(correo).matches();
            default -> true;
        };

        if (!valido) {
            String dominio = codigo.equals("USAL") ? "@usal.es" : "@upsa.es";
            throw new TfgValidationRuntimeException(new ErrorResponse[] {
                    ErrorResponse.builder()
                            .status("400")
                            .message("El email debe ser institucional (" + dominio + ") para " + universidad.getNombre())
                            .build()
            });
        }
    }
}
