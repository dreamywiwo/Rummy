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

        List<Ficha> base = fichas.stream()
                .map(FichaPlaced::getFicha)
                .collect(Collectors.toList());

        List<Ficha> normales = base.stream()
                .filter(f -> !f.isEsComodin())
                .collect(Collectors.toList());

        if (normales.isEmpty()) return true;

        String color = normales.get(0).getColor();
        for (Ficha f : normales)
            if (!f.getColor().equals(color)) return false;

        normales.sort(Comparator.comparingInt(Ficha::getNumero));

        int comodines = base.size() - normales.size();
        int expected = normales.get(0).getNumero();

        for (Ficha f : normales) {
            if (f.getNumero() == expected) {
                expected++;
            } else if (f.getNumero() > expected) {

                int gap = f.getNumero() - expected;

                if (gap > comodines) return false;

                comodines -= gap;
                expected = f.getNumero() + 1;

            } else {
                return false;
            }
        }

        return true;
    }
}
