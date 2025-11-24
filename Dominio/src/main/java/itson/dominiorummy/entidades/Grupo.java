package itson.dominiorummy.entidades;

import java.util.ArrayList;
import java.util.List;

public abstract class Grupo {

    protected final String id;
    protected final List<FichaPlaced> fichas;

    public Grupo(String id, List<FichaPlaced> fichasIniciales) {
        this.id = id;
        this.fichas = new ArrayList<>();
        if (fichasIniciales != null) {
            this.fichas.addAll(fichasIniciales);
        }
    }

    public String getId() {
        return id;
    }

    public List<FichaPlaced> getFichas() {
        return fichas;
    }

    public void limpiar() {
        fichas.clear();
    }

    public void agregarFicha(FichaPlaced fp) {
        fichas.add(fp);
    }

    public boolean estaVacio() {
        return fichas.isEmpty();
    }

    /** Validación de estructura (no valida tamaño mínimo). */
    public abstract boolean validarReglas();
    
    @Override
    public String toString() {
        return id + " (" + this.getClass().getSimpleName() + "): " + fichas;
    }

}
