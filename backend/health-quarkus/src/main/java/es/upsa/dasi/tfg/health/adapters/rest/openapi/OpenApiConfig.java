package es.upsa.dasi.tfg.health.adapters.rest.openapi;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API Microservicio Health",
                version = "1.0.0",
                description = "Comprobación de disponibilidad de los servicios del sistema."
        ),
        servers = @Server(url = "http://localhost:8081", description = "Desarrollo local")
)
public class OpenApiConfig
{
}
