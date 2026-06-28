package es.upsa.dasi.tfg.aggregator.adapters.rest.openapi;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API Aggregator (BFF)",
                version = "1.0.0",
                description = "Punto de entrada unificado de la aplicación. Agrega los microservicios y expone la API consumida por el frontend."
        ),
        servers = @Server(url = "http://localhost:8080", description = "Desarrollo local"),
        security = @SecurityRequirement(name = "firebase-jwt")
)
@SecurityScheme(
        securitySchemeName = "firebase-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Token JWT de Firebase. Cabecera: Authorization: Bearer <token>"
)
public class OpenApiConfig
{
}
