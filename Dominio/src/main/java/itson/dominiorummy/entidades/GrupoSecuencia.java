package itson.dominiorummy.entidades;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class GrupoSecuencia extends Grupo {

    public GrupoSecuencia(String id, List<FichaPlaced> fichas) {
        super(id, fichas);
    }

    @Override
    public boolean validarReglas() {
        // 1. Evitar bugs
        if (fichas.isEmpty()) {
            return false;
        }
        if (fichas.size() == 1) {
            return true; // 1 ficha es válida estructuralmente
        }
        List<Ficha> base = fichas.stream()
                .map(FichaPlaced::getFicha)
                .collect(Collectors.toList());

        List<Ficha> normales = base.stream()
                .filter(f -> !f.isEsComodin())
                .collect(Collectors.toList());

        // Si son puros comodines, es válido
        if (normales.isEmpty()) {
            return true;
        }

        // 2. Validar mismo color
        String color = normales.get(0).getColor();
        for (Ficha f : normales) {
            if (!f.getColor().equals(color)) {
                return false; // Estructura rota: Colores distintos
            }
        }

        // 3. Validar secuencia numérica
        normales.sort(Comparator.comparingInt(Ficha::getNumero));

        int comodines = base.size() - normales.size();
        int expected = normales.get(0).getNumero();

        for (Ficha f : normales) {
            if (f.getNumero() == expected) {
                expected++;
            } else if (f.getNumero() > expected) {
                // Hueco detectado, intentamos rellenar con comodines
                int gap = f.getNumero() - expected;
                if (gap > comodines) {
                    return false; // Estructura rota: Faltan comodines
                }
                comodines -= gap;
                expected = f.getNumero() + 1;
            } else {
                return false; // Estructura rota: Números duplicados o desorden
            }
        }

        return true;
    }

    @Override
    public int calcularPuntos() {
        if (fichas.isEmpty()) {
            return 0;
        }

        int primerNumeroNormal = -1;
        int indicePrimerNormal = -1;

        for (int i = 0; i < fichas.size(); i++) {
            if (!fichas.get(i).getFicha().isEsComodin()) {
                primerNumeroNormal = fichas.get(i).getFicha().getNumero();
                indicePrimerNormal = i;
                break;
            }
        }

        if (primerNumeroNormal == -1) {
            return 0;
        }

        int numeroInicio = primerNumeroNormal - indicePrimerNormal;

        int sumaTotal = 0;
        for (int i = 0; i < fichas.size(); i++) {
            sumaTotal += (numeroInicio + i);
        }

        return sumaTotal;
    }
}
