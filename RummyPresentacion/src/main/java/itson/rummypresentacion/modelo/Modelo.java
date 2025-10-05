/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.modelo;

import entidades.EstadoJuego;
import entidades.Grupo;
import entidades.Jugador;
import entidades.Tablero;
import entidades.Turno;
import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import itson.rummypresentacion.fachada.IFachadaDominio;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Dana Chavez
 */
public class Modelo implements IModelo, ISubject {

    private IFachadaDominio fachada;
    private int numeroTurno;
    private String idJugador;
    private List<IObserver> observers = new ArrayList<>();
    EstadoJuego estado = new EstadoJuego();
    Tablero tablero = new Tablero();

    public Modelo(IFachadaDominio fachada) {
        this.fachada = fachada;
    }

    public void terminarTurno(String jugadorId) throws Exception {
        fachada.terminarTurno(jugadorId);
        cargarDesdeFachada();
        notificarObservers();
    }

    public void pasarTurno() throws Exception {
        fachada.pasarTurno();
        cargarDesdeFachada();
        notificarObservers();
    }

    public void tomarFicha(String jugadorId) throws Exception {
        fachada.tomarFicha(jugadorId);
        cargarDesdeFachada();
        notificarObservers();
    }


    public void iniciarNuevaPartida(List<Jugador> jugadores) throws Exception {
        if (jugadores.isEmpty()) {
            throw new Exception("No se pudo iniciar la partida");
        }
        estado.getTablero().getGrupos().clear();
        estado.setJugadores(jugadores);
        Turno primerTurno = new Turno(1, jugadores.get(0));
        estado.setTurnoActual(primerTurno);
    }
    
    public GrupoDTO crearGrupo(List<FichaDTO> fichas) throws Exception {
        GrupoDTO grupo = fachada.crearGrupo(fichas);
        cargarDesdeFachada();
        notificarObservers();
        return grupo;
    }

    private Jugador getJugador(String jugadorId) throws Exception {
        if (jugadorId == null || jugadorId.isBlank()) {
            if (estado.getTurnoActual() != null && estado.getTurnoActual().getJugadorActual() != null) {
                return estado.getTurnoActual().getJugadorActual();
            }
            throw new Exception("No hay jugador actual y no se proporcionó jugadorId");
        }
        List<Jugador> jugadores = estado.getJugadores();
        if (jugadores == null || jugadores.isEmpty()) {
            throw new Exception("La lista de jugadores no está inicializada o está vacía");
        }
        for (Jugador j : jugadores) {
            if (Objects.equals(j.getId(), jugadorId) || Objects.equals(j.getNombre(), jugadorId)) {
                return j;
            }
        }
        throw new Exception("Jugador no encontrado: " + jugadorId);
    }

    private Grupo getGrupo(String grupoId) throws Exception {
        if (grupoId == null || grupoId.isBlank()) {
            throw new Exception("Grupo no especificado (grupoId vacío)");
        }

        if (estado.getTablero() == null) {
            estado.setTablero(new Tablero());
        }

        List<Grupo> grupos = estado.getTablero().getGrupos();
        if (grupos == null) {
            grupos = new ArrayList<>();
            estado.getTablero().setGrupos(grupos);
        }
        for (Grupo g : grupos) {
            if (Objects.equals(g.getId(), grupoId)) {
                return g;
            }
        }
        throw new Exception("Grupo no encontrado: " + grupoId);
    }

    private void cargarDesdeFachada() {
        this.numeroTurno = fachada.getNumeroTurno();
        this.idJugador = fachada.getJugadorActualId();
    }

    @Override
    public boolean esTurnoDelJugador(String jugadorId) {
        return fachada.esTurnoDelJugador(jugadorId);
    }

    @Override
    public String getJugadorActualId() {
        return idJugador;
    }

    @Override
    public int getTurnoNumero() {
        return numeroTurno;
    }

    @Override
    public void registrarJugador(String idJugador) {
        fachada.registrarJugador(idJugador);
        cargarDesdeFachada();
        notificarObservers();
    }

    @Override
    public List<String> getJugadores() {
        return fachada.getJugadores();
    }
     
    // Observer
    @Override
    public void suscribir(IObserver observer) {
        observers.add(observer);
    }

    @Override
    public void notificar(IObserver observer) {
        observer.update(this);
    }

    @Override
    public void notificarObservers() {
        for (IObserver observer : observers) {
            observer.update(this);
        }
    }

}
