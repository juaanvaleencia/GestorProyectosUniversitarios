package es.upsa.dasi.tfg.aggregator.infrastructure.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import org.eclipse.microprofile.rest.client.ext.ClientHeadersFactory;

@ApplicationScoped
public class ForwardAuthorizationFactory implements ClientHeadersFactory
{
    @Inject IncomingAuthHolder holder;

    @Override
    public MultivaluedMap<String, String> update(
            MultivaluedMap<String, String> incomingHeaders,
            MultivaluedMap<String, String> clientOutgoingHeaders)
    {
        MultivaluedMap<String, String> result = new MultivaluedHashMap<>();
        if (holder.getAuthorization() != null) {
            result.putSingle(HttpHeaders.AUTHORIZATION, holder.getAuthorization());
        }
        return result;
    }
}
