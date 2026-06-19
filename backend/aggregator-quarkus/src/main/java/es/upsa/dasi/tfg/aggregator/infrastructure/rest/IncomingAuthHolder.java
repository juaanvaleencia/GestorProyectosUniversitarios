package es.upsa.dasi.tfg.aggregator.infrastructure.rest;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class IncomingAuthHolder
{
    private String authorization;

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
