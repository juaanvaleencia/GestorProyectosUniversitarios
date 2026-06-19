package es.upsa.dasi.tfg.aggregator.infrastructure.rest;

import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
public class CaptureAuthFilter implements ContainerRequestFilter
{
    @Inject IncomingAuthHolder holder;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        holder.setAuthorization(requestContext.getHeaderString("Authorization"));
    }
}
