/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.modelo;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import itson.rummypresentacion.fachada.IFachadaDominio;
import itson.rummypresentacion.fachada.ResultadoJugada;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class Modelo implements IModelo, ISubject {

    private IFachadaDominio fachada;
    private List<IObserver> observers = new ArrayList<>();

    // Estado interno para la UI
    private List<String> fichasSeleccionadas = new ArrayList<>();
    private String ultimoEvento = "INICIALIZADO";
    private String ultimoError;

    public Modelo(IFachadaDominio fachada) {
        this.fachada = fachada;
    }

    // Llamadas de control
    public void colocarFichasEnTablero(String jugadorId, List<FichaDTO> fichas, Point posicion) {
        try {
            System.out.println("DEBUG - Modelo.colocarFichasEnTablero:");
            System.out.println("  Jugador: " + jugadorId);
            System.out.println("  Fichas: " + fichas.size());
            System.out.println("  Posición: " + posicion);

            ResultadoJugada resultado = fachada.colocarFichas(jugadorId, fichas, posicion);

            if (resultado.esExitoso()) {
                fichasSeleccionadas.clear();
                ultimoEvento = "FICHAS_COLOCADAS";
                ultimoError = null;
                if (resultado.getMensaje().contains("30 puntos")) {
                    ultimoEvento = "PRIMER_BAJADA";
                } else {
                    ultimoEvento = "FICHAS_COLOCADAS";
                }
            } else {
                ultimoError = resultado.getMensaje();
                ultimoEvento = "ERROR_COLOCACION";
            }
            notificarObservers();


        } catch (Exception e) {
            ultimoError = "Error al colocar fichas: " + e.getMessage();
            ultimoEvento = "ERROR_SISTEMA";
            notificarObservers();
        }
    }

    // En otros métodos, solo notificar si hubo cambios reales
    public void terminarTurno(String jugadorId) {
        try {
            fachada.terminarTurno(jugadorId);
            fichasSeleccionadas.clear(); // Limpiar selecciones al terminar turno
            ultimoEvento = "TURNO_TERMINADO";
            ultimoError = null;
            notificarObservers();

        } catch (Exception e) {
            ultimoError = e.getMessage();
            ultimoEvento = "ERROR_TERMINAR_TURNO";
            notificarObservers();
        }
    }

    public void tomarFicha(String jugadorId) {
        try {
            fachada.tomarFicha(jugadorId);
            ultimoEvento = "FICHA_TOMADA";
            ultimoError = null;
            notificarObservers();

        } catch (Exception e) {
            ultimoError = "Error al tomar ficha: " + e.getMessage();
            ultimoEvento = "ERROR_TOMAR_FICHA";
            notificarObservers();
        }
    }

    public void pasarTurno() {
        try {
            fachada.pasarTurno();
            fichasSeleccionadas.clear();
            ultimoEvento = "TURNO_CAMBIADO";
            ultimoError = null;
            notificarObservers();

        } catch (Exception e) {
            ultimoError = "Error al pasar turno: " + e.getMessage();
            ultimoEvento = "ERROR_CAMBIAR_TURNO";
            notificarObservers();
        }
    }

    public void toggleSeleccionFicha(String fichaId) {
        // Solo notificar si realmente hubo un cambio
        boolean cambio = false;
        if (fichasSeleccionadas.contains(fichaId)) {
            fichasSeleccionadas.remove(fichaId);
            cambio = true;
        } else {
            fichasSeleccionadas.add(fichaId);
            cambio = true;
        }

        if (cambio) {
            ultimoEvento = "SELECCION_CAMBIADA";
            notificarObservers();
        }
    }

    public void crearGrupo(List<FichaDTO> fichas) {
        try {
            fachada.crearGrupo(fichas);
            fichasSeleccionadas.clear();
            ultimoEvento = "GRUPO_CREADO";
            ultimoError = null;
            notificarObservers();

        } catch (Exception e) {
            ultimoError = "Error al crear grupo: " + e.getMessage();
            ultimoEvento = "ERROR_CREAR_GRUPO";
            notificarObservers();
        }
    }

    // Inicializacion
    public void inicializarJuego(List<FichaDTO> manoJugador1, List<FichaDTO> manoJugador2, List<FichaDTO> pozoFichas) {
        try {
            // Registrar jugadores
            fachada.registrarJugador("Jugador1");
            fachada.registrarJugador("Jugador2");

            // Inicializar juego en la fachada
            fachada.inicializarJuego(manoJugador1, manoJugador2, pozoFichas);

            ultimoEvento = "JUEGO_INICIALIZADO";
            ultimoError = null;
            notificarObservers();

        } catch (Exception e) {
            ultimoError = "Error al inicializar juego: " + e.getMessage();
            ultimoEvento = "ERROR_INICIALIZACION";
            notificarObservers();
        }
    }

    // IModelo (para pasar a la vista solo lo necesario)
    @Override
    public String getJugadorActivoId() {
        return fachada.getJugadorActualId();
    }

    @Override
    public List<FichaDTO> getManoJugadorActual() {
        List<FichaDTO> mano = fachada.obtenerManoJugadorActual();
        System.out.println("DEBUG - Modelo.getManoJugadorActual(): "
                + (mano != null ? mano.size() : "null") + " fichas");
        if (mano != null) {
            for (FichaDTO ficha : mano) {
                System.out.println("  - " + ficha.getId() + " (" + ficha.getNumero() + ", " + ficha.getColor() + ")");
            }
        }
        return mano;
    }

    @Override
    public List<GrupoDTO> getGruposTablero() {
        List<GrupoDTO> grupos = fachada.obtenerGruposTablero();
        System.out.println("DEBUG - Modelo.getGruposTablero(): "
                + (grupos != null ? grupos.size() : "null") + "grupos");
        return grupos;
    }

    @Override
    public int getTurnoActual() {
        return fachada.getNumeroTurno();
    }

    @Override
    public boolean esTurnoDe(String jugadorId) {
        return fachada.esTurnoDelJugador(jugadorId);
    }

    @Override
    public List<String> getFichasSeleccionadas() {
        return new ArrayList<>(fichasSeleccionadas);
    }

    @Override
    public String getUltimoEvento() {
        return ultimoEvento;
    }

    @Override
    public String getUltimoError() {
        return ultimoError;
    }

    @Override
    public long getTimestamp() {
        return System.currentTimeMillis();
    }

    @Override
    public boolean isJuegoTerminado() {
        // TODO
        return false;
    }

    @Override
    public String getGanador() {
        // TODO
        return null;
    }

    @Override
    public void registrarJugador(String idJugador) {
        fachada.registrarJugador(idJugador);
    }

    @Override
    public List<String> getJugadores() {
        return fachada.getJugadores();
    }

    // Observer
    @Override
    public void suscribir(IObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
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

    // Utils
    public void limpiarErrores() {
        this.ultimoError = null;
    }

    public void limpiarSeleccion() {
        this.fichasSeleccionadas.clear();
    }

    @Override
    public List<FichaDTO> getManoDe(String jugadorId) {
        return fachada.obtenerManoJugador(jugadorId);
    }
}
