package es.upsa.dasi.tfg.aggregator.adapters.rest.dto;

public record UsuarioSync(String firebaseUid, String email, String nombre, String avatarUrl) {}
