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

            if (valorRef == null) valorRef = f.getNumero();
            else if (!valorRef.equals(f.getNumero())) return false;

            colores.add(f.getColor());
        }

        return colores.size() == (base.size() - comodines);
    }
}
