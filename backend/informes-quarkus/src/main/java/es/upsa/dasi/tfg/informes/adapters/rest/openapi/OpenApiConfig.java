package es.upsa.dasi.tfg.informes.adapters.rest.openapi;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API Microservicio Informes",
                version = "1.0.0",
                description = "Resúmenes estadísticos de actividad en proyectos del usuario."
        ),
        servers = @Server(url = "http://localhost:8085", description = "Desarrollo local"),
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
