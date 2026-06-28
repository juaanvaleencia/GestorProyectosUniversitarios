package es.upsa.dasi.tfg.tareas.adapters.rest.openapi;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API Microservicio Tareas",
                version = "1.0.0",
                description = "Gestión de tareas y subtareas asociadas a proyectos."
        ),
        servers = @Server(url = "http://localhost:8083", description = "Desarrollo local"),
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
