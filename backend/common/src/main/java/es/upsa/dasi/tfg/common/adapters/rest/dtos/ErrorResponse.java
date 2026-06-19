package es.upsa.dasi.tfg.common.adapters.rest.dtos;

import lombok.Builder;

@Builder
public record ErrorResponse(String status, String message) {}
