package itson.dominiorummy.entidades;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GrupoNumero extends Grupo {

    public GrupoNumero(String id, List<FichaPlaced> fichas) {
        super(id, fichas);
    }

    @Override
    public boolean validarReglas() {
        // 1. Evitar bugs con listas vacías o de 1 elemento
        if (fichas.isEmpty()) {
            return false;
        }
        if (fichas.size() == 1) {
            return true; // 1 ficha técnicamente es válida estructuralmente
        }
        List<Ficha> base = fichas.stream()
                .map(FichaPlaced::getFicha)
                .collect(Collectors.toList());

        Integer valorRef = null;
        Set<String> colores = new HashSet<>();
        int comodines = 0;

        for (Ficha f : base) {
            if (f.isEsComodin()) {
                comodines++;
                continue;
            }

            // Validar que todos tengan el mismo número
            if (valorRef == null) {
                valorRef = f.getNumero();
            } else if (!valorRef.equals(f.getNumero())) {
                return false; // Estructura rota: Números distintos
            }

            colores.add(f.getColor());
        }

        // Validar que no haya colores repetidos
        // (Cantidad de colores únicos debe ser igual a cantidad de fichas no-comodín)
        return colores.size() == (base.size() - comodines);
    }

    @Override
    public int calcularPuntos() {
        if (fichas.isEmpty()) {
            return 0;
        }

        int valorReferencia = 0;
        for (FichaPlaced fp : fichas) {
            if (!fp.getFicha().isEsComodin()) {
                valorReferencia = fp.getFicha().getNumero();
                break;
            }
        }

        return valorReferencia * fichas.size();
    }
}
