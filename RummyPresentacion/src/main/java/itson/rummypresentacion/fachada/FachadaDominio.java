/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.fachada;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class FachadaDominio implements IFachadaDominio {

    private final List<String> jugadores = new ArrayList<>();
    private int numeroTurno = 0;
    private String idJugadorActual = null;
    private final List<String> pozoFichas = new ArrayList<>();
    private final List<String> fichasA = new ArrayList<>();
    private final List<String> fichasB = new ArrayList<>();

    @Override
    public void terminarTurno(String idJugador) throws Exception {
        if (!esTurnoDelJugador(idJugador)) {
            throw new Exception("No es tu turno, jugador " + idJugador);
        }
        System.out.println("Turno finalizado, jugador: " + getJugadorActualId() + ", sigue");
    }

    @Override
    public void tomarFicha(String jugadorId) throws Exception {
        if (!esTurnoDelJugador(jugadorId)) {
            throw new Exception("No es tu turno, jugador " + jugadorId);
        }
        if (pozoFichas.isEmpty()) {
            throw new Exception("No hay fichas en el pozo");
        }
        String ficha = pozoFichas.remove(0);
        if (jugadorId.equals("Jugador1")) {
            fichasA.add(ficha);
        } else {
            fichasB.add(ficha);
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
}
