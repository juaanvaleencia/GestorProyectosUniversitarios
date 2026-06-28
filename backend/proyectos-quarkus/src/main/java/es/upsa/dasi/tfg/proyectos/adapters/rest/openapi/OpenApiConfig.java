package es.upsa.dasi.tfg.proyectos.adapters.rest.openapi;

import org.eclipse.microprofile.openapi.annotations.OpenAPIDefinition;
import org.eclipse.microprofile.openapi.annotations.enums.SecuritySchemeType;
import org.eclipse.microprofile.openapi.annotations.info.Info;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityScheme;
import org.eclipse.microprofile.openapi.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "API Microservicio Proyectos",
                version = "1.0.0",
                description = "Gestión de proyectos, miembros, invitaciones, hitos y catálogo de plantillas."
        ),
        servers = @Server(url = "http://localhost:8082", description = "Desarrollo local"),
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
