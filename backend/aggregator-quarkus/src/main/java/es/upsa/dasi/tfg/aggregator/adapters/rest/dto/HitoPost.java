package es.upsa.dasi.tfg.aggregator.adapters.rest.dto;

public record HitoPost(
        String titulo,
        String fecha,
        boolean completado
) {}
