package es.upsa.dasi.tfg.common.domain.model;

import java.util.Arrays;

public enum RolProyecto
{
    PRODUCT_OWNER("Product Owner"),
    SCRUM_MASTER("Scrum Master"),
    DEVELOPER("Equipo de Desarrollo");

    private final String etiqueta;

    RolProyecto(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static String etiquetaDe(String codigo) {
        if (codigo == null) return "";
        return Arrays.stream(values())
                .filter(r -> r.name().equals(codigo))
                .findFirst()
                .map(RolProyecto::getEtiqueta)
                .orElse(codigo);
    }
}
