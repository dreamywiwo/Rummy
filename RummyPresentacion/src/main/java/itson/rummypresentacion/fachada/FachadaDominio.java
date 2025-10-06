/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.fachada;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import itson.rummypresentacion.modelo.IModelo;
import itson.rummypresentacion.modelo.Modelo;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Dana Chavez
 */
public class FachadaDominio implements IFachadaDominio {

    private final List<String> jugadores = new ArrayList<>();
    private final List<Integer> turnoPropio = new ArrayList<>();
    private final List<Integer> puntosIniciales = new ArrayList<>();
    private int numeroTurno = 0;
    private String idJugadorActual = null;

    private List<GrupoDTO> gruposTablero = new ArrayList<>();
    private Map<String, List<FichaDTO>> manosJugadores = new HashMap<>();
    private List<FichaDTO> pozoFichas = new ArrayList<>();
    private Map<GrupoDTO, Point> posicionesGrupos = new HashMap<>();

    @Override
    public void inicializarJuego(List<FichaDTO> manoJugador1, List<FichaDTO> manoJugador2, List<FichaDTO> pozo) {
        System.out.println("DEBUG - Fachada.inicializarJuego INICIO");

        // Limpiar estado anterior
        gruposTablero.clear();
        posicionesGrupos.clear();
        manosJugadores.clear();

        // Configurar manos
        manosJugadores.put("Jugador1", new ArrayList<>(manoJugador1));
        manosJugadores.put("Jugador2", new ArrayList<>(manoJugador2));

        // Configurar pozo
        this.pozoFichas = new ArrayList<>(pozo);

        // Inicializar turno
        this.numeroTurno = 0;
        this.idJugadorActual = "Jugador1";
        for (int i = 0; i < jugadores.size(); i++) {
            turnoPropio.add(0);
            puntosIniciales.add(0);
        }

        System.out.println("DEBUG - Fachada.inicializarJuego FIN");
        System.out.println("  - Mano Jugador1: " + manoJugador1.size() + " fichas");
        System.out.println("  - Mano Jugador2: " + manoJugador2.size() + " fichas");
        System.out.println("  - Pozo: " + pozoFichas.size() + " fichas");
    }

    @Override
    public void registrarJugador(String idJugador) {
        if (idJugador == null || idJugador.isBlank()) {
            return;
        }
        if (!jugadores.contains(idJugador)) {
            jugadores.add(idJugador);
            if (idJugadorActual == null) {
                idJugadorActual = idJugador;
            }
            System.out.println("Jugador registrado: " + idJugador);
        }
    }

    @Override
    public boolean pasarTurno() {
        if (pozoFichas.isEmpty()) {
            numeroTurno = (numeroTurno + 1) % jugadores.size();
            return false;
        }
        numeroTurno = (numeroTurno + 1) % jugadores.size();
        return true;
    }

    @Override
    public void tomarFicha(String jugadorId) throws Exception {
        if (!esTurnoDelJugador(jugadorId)) {
            throw new Exception("No es tu turno, jugador " + jugadorId);
        }
        if (pozoFichas.isEmpty()) {
            throw new Exception("No hay fichas en el pozo");
        }
        FichaDTO ficha = pozoFichas.remove(0);

        // CORRECCIÓN: Usar el mapa de manos en lugar de las listas separadas
        List<FichaDTO> mano = manosJugadores.get(jugadorId);
        if (mano != null) {
            mano.add(ficha);
        } else {
            throw new Exception("Jugador no registrado: " + jugadorId);
        }
    }

    @Override
    public ResultadoJugada colocarFichas(String jugadorId, List<FichaDTO> fichas, Point posicion) throws Exception {
        if (!esTurnoDelJugador(jugadorId)) {
            return new ResultadoJugada(false, "No es tu turno");
        }

        if (!manoContieneFichas(jugadorId, fichas)) {
            return new ResultadoJugada(false, "No tienes estas fichas en tu mano");
        }
        GrupoDTO grupoAdyacente = encontrarGrupoAdyacente(posicion);

        if (grupoAdyacente != null) {
            return unirAGrupoExistente(grupoAdyacente, fichas, jugadorId);
        } else {
            return crearNuevoGrupoEnTablero(fichas, posicion, jugadorId);
        }
    }

    private int sumaDeFichas(List<FichaDTO> fichas, String jugadorId) {
        int total = 0;
        for (int i = 0; i < fichas.size(); i++) {
            total = total + fichas.get(i).getNumero();
        }
        return total;
    }

    private boolean manoContieneFichas(String jugadorId, List<FichaDTO> fichas) {
        List<FichaDTO> mano = manosJugadores.get(jugadorId);

        System.out.println("DEBUG - manoContieneFichas para jugador: " + jugadorId);
        System.out.println("DEBUG - Mano del jugador: " + (mano != null ? mano.size() : "null") + " fichas");
        System.out.println("DEBUG - Fichas a verificar: " + fichas.size() + " fichas");

        if (mano == null) {
            System.out.println("DEBUG - Mano es null!");
            return false;
        }

        // Verificar cada ficha individualmente
        for (FichaDTO ficha : fichas) {
            boolean contiene = mano.contains(ficha);
            System.out.println("DEBUG - Ficha " + ficha.getId() + " (" + ficha.getNumero() + ", " + ficha.getColor() + ") en mano: " + contiene);
            if (!contiene) {
                // Debug adicional: mostrar qué fichas hay en la mano
                System.out.println("DEBUG - Fichas en mano:");
                for (FichaDTO fichaMano : mano) {
                    System.out.println("  - " + fichaMano.getId() + " (" + fichaMano.getNumero() + ", " + fichaMano.getColor() + ")");
                }
                return false;
            }
        }

        return true;
    }

    private GrupoDTO encontrarGrupoAdyacente(Point posicion) {
        for (Map.Entry<GrupoDTO, Point> entry : posicionesGrupos.entrySet()) {
            Point posGrupo = entry.getValue();
            // Lógica para encontrar grupo adyacente (horizontal o vertical)
            if (esAdyacente(posGrupo, posicion, entry.getKey().getFichas().size())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private boolean esAdyacente(Point posGrupo, Point posicion, int tamañoGrupo) {
        // Misma fila y columna adyacente
        boolean mismaFila = posGrupo.x == posicion.x;
        boolean columnaAdyacente = Math.abs(posGrupo.y - posicion.y) <= tamañoGrupo;

        // Misma columna y fila adyacente
        boolean mismaColumna = posGrupo.y == posicion.y;
        boolean filaAdyacente = Math.abs(posGrupo.x - posicion.x) == 1;

        return (mismaFila && columnaAdyacente) || (mismaColumna && filaAdyacente);
    }

    private ResultadoJugada unirAGrupoExistente(GrupoDTO grupoExistente, List<FichaDTO> fichas, String jugadorId) throws Exception {
        List<FichaDTO> fichasTemporal = new ArrayList<>(grupoExistente.getFichas());
        fichasTemporal.addAll(fichas);

        boolean esSecuencia = validarSecuencia(fichasTemporal);
        boolean esMismoNumero = validarNumeroIgual(fichasTemporal);

        if (esSecuencia || esMismoNumero) {
            // Actualizar grupo existente
            grupoExistente.getFichas().addAll(fichas);
            removerFichasDeMano(jugadorId, fichas);

            // SOLO éxito y mensaje, NO estadoActual
            return new ResultadoJugada(true, "Fichas unidas al grupo existente");
        } else {
            Point nuevaPosicion = encontrarPosicionParaNuevoGrupo(fichas.size());
            if (nuevaPosicion != null) {
                return crearNuevoGrupoEnTablero(fichas, nuevaPosicion, jugadorId);
            } else {
                throw new Exception("No se puede unir al grupo y no hay espacio para nuevo grupo");
            }
        }
    }

    private ResultadoJugada crearNuevoGrupoEnTablero(List<FichaDTO> fichas, Point posicion, String jugadorId) throws Exception {
        GrupoDTO nuevoGrupo = crearGrupo(fichas);
        posicionesGrupos.put(nuevoGrupo, posicion);
        gruposTablero.add(nuevoGrupo);
        removerFichasDeMano(jugadorId, fichas);

        // SOLO éxito y mensaje, NO estadoActual
        return new ResultadoJugada(true, "Nuevo grupo creado en el tablero");
    }

    private void removerFichasDeMano(String jugadorId, List<FichaDTO> fichas) {
        List<FichaDTO> mano = manosJugadores.get(jugadorId);
        mano.removeAll(fichas);
    }

    private Point encontrarPosicionParaNuevoGrupo(int tamañoGrupo) {
        // Buscar en el tablero 4x10
        for (int fila = 0; fila < 4; fila++) {
            for (int columna = 0; columna <= 10 - tamañoGrupo; columna++) {
                Point posicion = new Point(fila, columna);
                if (esPosicionDisponible(posicion, tamañoGrupo)) {
                    return posicion;
                }
            }
        }
        return null; // No hay espacio
    }

    private boolean esPosicionDisponible(Point posicion, int tamaño) {
        for (int i = 0; i < tamaño; i++) {
            Point puntoVerificar = new Point(posicion.x, posicion.y + i);
            if (estaOcupado(puntoVerificar)) {
                return false;
            }
        }
        return true;
    }

    private boolean estaOcupado(Point punto) {
        return posicionesGrupos.values().stream()
                .anyMatch(pos -> pos.equals(punto));
    }

    @Override
    public GrupoDTO crearGrupo(List<FichaDTO> fichas) throws Exception {
        if (fichas == null) {
            throw new Exception("No hay fichas para ese grupo.");
        }
        boolean esSecuencia = validarSecuencia(fichas);
        boolean esMismoNumero = validarNumeroIgual(fichas);
        if (!esSecuencia && !esMismoNumero) {
            throw new Exception("Grupo inválido: debe ser secuencia del mismo color o del mismo número con colores distintos.");
        }
        return new GrupoDTO(new ArrayList<>(fichas), true);
    }

    public GrupoDTO agregarFichaAGrupo(FichaDTO ficha, GrupoDTO grupo) throws Exception {
        if (grupo == null) {
            throw new Exception("Debe de existir un grupo al cual anadir la ficha.");
        }
        List<FichaDTO> fichas = grupo.getFichas();
        fichas.add(ficha);

        boolean esSecuencia = validarSecuencia(fichas);
        boolean esMismoNumero = validarNumeroIgual(fichas);
        if (!esSecuencia && !esMismoNumero) {
            throw new Exception("Grupo inválido: debe ser secuencia del mismo color o del mismo número con colores distintos.");
        }

        return new GrupoDTO(new ArrayList<>(fichas), true);
    }

    public GrupoDTO eliminarFichaDeGrupo(FichaDTO ficha, GrupoDTO grupo) throws Exception {

        if (grupo == null) {
            throw new Exception("No hay grupo del cual eliminar fichas.");
        }

        List<FichaDTO> fichas = grupo.getFichas();
        if (!fichas.contains(ficha)) {
            throw new Exception("el grupo no contiene la ficha seleccionada");
        }

        fichas.remove(ficha);
        boolean esSecuencia = validarSecuencia(fichas);
        boolean esMismoNumero = validarNumeroIgual(fichas);
        if (!esSecuencia && !esMismoNumero) {
            throw new Exception("Grupo inválido: debe ser secuencia del mismo color o del mismo número con colores distintos.");
        }

        return new GrupoDTO(new ArrayList<>(fichas), true);
    }

    @Override
    public int getNumeroTurno() {
        return numeroTurno;
    }

    @Override
    public String getJugadorActualId() {
        return jugadores.get(numeroTurno);
    }

    @Override
    public boolean esTurnoDelJugador(String idJugador) {
        return jugadores.get(numeroTurno).equals(idJugador);
    }

    public List<String> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    /* Validaciones grupos */
    public boolean validarSecuencia(List<FichaDTO> fichas) throws Exception {
        if (fichas == null || fichas.isEmpty()) {
            throw new Exception("La lista está vacía.");
        }

        String colorBase = fichas.get(0).getColor();
        for (FichaDTO f : fichas) {
            if (!f.getColor().equals(colorBase)) {
                return false;
            }
        }

        List<Integer> numeros = new ArrayList<>();
        for (FichaDTO f : fichas) {
            numeros.add(f.getNumero());
        }

        Collections.sort(numeros);
        for (int i = 1; i < numeros.size(); i++) {
            if (numeros.get(i) != numeros.get(i - 1) + 1) {
                return false;
            }
        }
        return true;
    }

    public boolean validarNumeroIgual(List<FichaDTO> fichas) throws Exception {
        if (fichas == null || fichas.isEmpty()) {
            throw new Exception("La lista está vacía.");
        }
        int numeroBase = fichas.get(0).getNumero();
        for (FichaDTO f : fichas) {
            if (f.getNumero() != numeroBase) {
                return false;
            }
        }
        for (int i = 0; i < fichas.size(); i++) {
            for (int j = i + 1; j < fichas.size(); j++) {
                if (fichas.get(i).getColor().equals(fichas.get(j).getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void terminarTurno(String idJugador) throws Exception {
        if (!esTurnoDelJugador(idJugador)) {
            throw new Exception("No es tu turno, jugador " + idJugador);
        }

        List<String> problemas = validarEstadoFinalTablero();
        if (!problemas.isEmpty()) {
            throw new Exception("No se puede terminar el turno:\n" + String.join("\n", problemas));
        }

        // Cambiar turno
        pasarTurno();
        System.out.println("Turno finalizado, jugador: " + getJugadorActualId() + ", sigue");
    }

    private List<String> validarEstadoFinalTablero() {
        List<String> problemas = new ArrayList<>();

        // Validar que todos los grupos tienen al menos 3 fichas
        for (GrupoDTO grupo : gruposTablero) {
            if (grupo.getFichas().size() < 3) {
                problemas.add("El grupo tiene solo " + grupo.getFichas().size() + " fichas (mínimo 3)");
            }
        }

        // Validar que todos los grupos son válidos
        for (GrupoDTO grupo : gruposTablero) {
            if (!esGrupoValido(grupo)) {
                problemas.add("Hay grupos inválidos en el tablero");
            }
        }

        return problemas;
    }

    private boolean esGrupoValido(GrupoDTO grupo) {
        try {
            return validarSecuencia(grupo.getFichas()) || validarNumeroIgual(grupo.getFichas());
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<FichaDTO> obtenerManoJugadorActual() {
        String jugadorActual = getJugadorActualId();
        List<FichaDTO> mano = manosJugadores.get(jugadorActual);
        System.out.println("DEBUG - Fachada.obtenerManoJugadorActual() para " + jugadorActual + ": "
                + (mano != null ? mano.size() : "null") + " fichas");
        return mano != null ? new ArrayList<>(mano) : new ArrayList<>();
    }

    @Override
    public List<GrupoDTO> obtenerGruposTablero() {
        return new ArrayList<>(gruposTablero);
    }

    @Override
    public List<FichaDTO> obtenerManoJugador(String jugadorId) {
        List<FichaDTO> mano = manosJugadores.get(jugadorId);
        return mano != null ? new ArrayList<>(mano) : new ArrayList<>();
    }

}
