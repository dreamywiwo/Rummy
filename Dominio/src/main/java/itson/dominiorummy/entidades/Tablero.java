package itson.dominiorummy.entidades;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tablero {

    private final Map<String, Grupo> grupos = new HashMap<>();
    private int contadorGrupos = 0;

    private final Map<String, String> removedOrigin = new HashMap<>();

    public Collection<Grupo> getGrupos() {
        return grupos.values();
    }

    public Grupo getGrupo(String id) {
        return grupos.get(id);
    }

    public String generarIdGrupo() {
        return "grupo_" + (contadorGrupos++);
    }

    public void agregarGrupo(Grupo grupo) {
        grupos.put(grupo.getId(), grupo);
    }

    public void removerGrupo(String grupoId) {
        grupos.remove(grupoId);
    }

    public boolean grupoEstaVacio(String grupoId) {
        Grupo g = grupos.get(grupoId);
        return g != null && g.estaVacio();
    }

    public void limpiarGrupo(String grupoId) {
        Grupo g = grupos.get(grupoId);
        if (g != null) {
            g.limpiar();
        }
    }

    public void agregarFichaAGrupo(String grupoId, FichaPlaced fp) {
        Grupo g = grupos.get(grupoId);
        if (g != null) {
            g.agregarFicha(fp);
        }
    }

    public List<FichaPlaced> obtenerFichasDeGrupo(String grupoId) {
        Grupo g = grupos.get(grupoId);
        if (g == null) {
            return Collections.emptyList();
        }

        return g.getFichas().stream()
                .map(FichaPlaced::clonar)
                .collect(Collectors.toList());
    }

    public FichaPlaced buscarFichaPlacedEnOtrosGrupos(String grupoOrigen, String fichaId) {

        for (Grupo g : grupos.values()) {
            if (g.getId().equals(grupoOrigen)) {
                continue;
            }

            Iterator<FichaPlaced> it = g.getFichas().iterator();
            while (it.hasNext()) {
                FichaPlaced fp = it.next();
                if (fp.getFicha().getId().equals(fichaId)) {
                    it.remove();
                    return fp;
                }
            }
        }

        return null;
    }

    public boolean validarReglasDeGrupo(String grupoId) {
        Grupo g = grupos.get(grupoId);
        if (g == null) {
            return false;
        }
        return g.validarReglas();
    }

    public Grupo crearGrupoDesdeFichasPlaced(List<FichaPlaced> fichas) {

        GrupoNumero numTmp = new GrupoNumero("tmp", fichas);
        GrupoSecuencia seqTmp = new GrupoSecuencia("tmp", fichas);

        boolean esNumero = numTmp.validarReglas();
        boolean esSecuencia = seqTmp.validarReglas();

        String nuevoId = generarIdGrupo();

        if (esNumero && !esSecuencia) {
            return new GrupoNumero(nuevoId, fichas);
        }
        if (esSecuencia && !esNumero) {
            return new GrupoSecuencia(nuevoId, fichas);
        }
        if (esSecuencia) {
            return new GrupoSecuencia(nuevoId, fichas);
        }

        return null;
    }

    /**
     * Busca la FichaPlaced en TODO el tablero (no realiza ninguna
     * modificación). Retorna la instancia encontrada o null si no existe.
     */
    public FichaPlaced buscarFichaPlacedGlobal(String fichaId) {
        for (Grupo g : grupos.values()) {
            for (FichaPlaced fp : g.getFichas()) {
                if (fp.getFicha().getId().equals(fichaId)) {
                    return fp;
                }
            }
        }
        return null;
    }

    /**
     * Remueve una ficha (por id) de su grupo actual en el tablero. Registra el
     * grupo de origen para permitir restaurarla si hace falta. Si la ficha no
     * existe, no hace nada.
     */
    public void removerFichaGlobal(String fichaId) {
        for (Grupo g : grupos.values()) {
            Iterator<FichaPlaced> it = g.getFichas().iterator();
            while (it.hasNext()) {
                FichaPlaced fp = it.next();
                if (fp.getFicha().getId().equals(fichaId)) {
                    // registrar origen y remover
                    removedOrigin.put(fichaId, g.getId());
                    it.remove();
                    return;
                }
            }
        }
    }

    /**
     * Restaura una ficha al grupo en el que estaba originalmente (si está
     * registrado). Si el grupo original ya no existe, se crea un nuevo grupo
     * con id generado y se agrega la ficha ahí para que no se pierda.
     *
     * NOTA: normalmente removedOrigin debería contener el grupo original si la
     * ficha se removió previamente mediante removerFichaGlobal(...).
     */
    public void restaurarFicha(FichaPlaced fp) {
        if (fp == null) {
            return;
        }

        String fichaId = fp.getFicha().getId();
        String origen = removedOrigin.remove(fichaId);

        if (origen != null) {
            Grupo g = grupos.get(origen);
            if (g != null) {
                g.agregarFicha(fp);
                return;
            }
        }

        // Si no tenemos origen registrado o el grupo ya no existe,
        // creamos un nuevo grupo con id generado y agregamos la ficha.
        // Preferimos crear un GrupoSecuencia como contenedor neutral;
        // si quieres otra política (p. ej. GrupoNumero o deducir por la ficha),
        // cámbialo aquí.
        List<FichaPlaced> lista = new ArrayList<>();
        lista.add(fp);
        String nuevoId = generarIdGrupo();
        Grupo nuevo = new GrupoSecuencia(nuevoId, lista);
        agregarGrupo(nuevo);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== TABLERO ===\n");

        if (grupos.isEmpty()) {
            sb.append("(sin grupos)\n");
        } else {
            for (Grupo g : grupos.values()) {
                sb.append(" - ").append(g.toString()).append("\n");
            }
        }

        sb.append("================\n");
        return sb.toString();
    }

}
