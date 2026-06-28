package es.upsa.dasi.tfg.aggregator.adapters.rest.dto;

public record TareaPut(
        String titulo,
        String descripcion,
        String estado,
        String prioridad,
        String responsableUid,
        String fechaLimite,
        int orden
) {}
