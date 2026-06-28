package es.upsa.dasi.tfg.aggregator.adapters.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MiembroInvite(
        @NotBlank @Email String email,
        @NotBlank String rol
) {}
