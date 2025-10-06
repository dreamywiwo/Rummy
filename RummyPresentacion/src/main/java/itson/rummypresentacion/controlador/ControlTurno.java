/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.controlador;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.modelo.Modelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.awt.Point;
import java.util.List;

public class ControlTurno {

    private final UI_TurnoJugador vista;
    private final Modelo modelo;

    public ControlTurno(UI_TurnoJugador vista, Modelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }

    // Cuando se seleccionen fichas

    public void toggleSeleccionFicha(String fichaId) {
        if (fichaId == null || fichaId.isBlank()) {
            return;
        }
        modelo.toggleSeleccionFicha(fichaId);
    }

    public void limpiarSeleccion() {
        modelo.limpiarSeleccion();
    }

    // Juego

    public void colocarFichasEnTablero(String jugadorId, List<FichaDTO> fichas, Point posicion) {
        if (!modelo.esTurnoDe(jugadorId)) {
            vista.mostrarMensaje("No es tu turno");
            return;
        }
        
        if (fichas == null || fichas.isEmpty()) {
            vista.mostrarMensaje("Selecciona fichas primero");
            return;
        }
        
        if (posicion == null) {
            vista.mostrarMensaje("Selecciona una posición en el tablero");
            return;
        }
        
        System.out.println("DEBUG - ControlTurno.colocarFichasEnTablero:");
        System.out.println("  Jugador: " + jugadorId);
        System.out.println("  Fichas: " + fichas.size());
        for (FichaDTO ficha : fichas) {
            System.out.println("    - " + ficha.getId() + " (" + ficha.getNumero() + ", " + ficha.getColor() + ")");
        }
        System.out.println("  Posición: " + posicion);
        
        modelo.colocarFichasEnTablero(jugadorId, fichas, posicion);
    }

    public void terminarTurno(String jugadorId) {
        if (!modelo.esTurnoDe(jugadorId)) {
            vista.mostrarMensaje("No es tu turno");
            return;
        }
        
        modelo.terminarTurno(jugadorId);
    }

    public void tomarFicha(String jugadorId) {
        if (!modelo.esTurnoDe(jugadorId)) {
            vista.mostrarMensaje("No es tu turno");
            return;
        }
        
        modelo.tomarFicha(jugadorId);
    }

    public void crearGrupo(List<FichaDTO> fichas) {
        if (fichas == null || fichas.isEmpty()) {
            vista.mostrarMensaje("Selecciona fichas para crear un grupo");
            return;
        }
        
        if (fichas.size() < 3) {
            vista.mostrarMensaje("Un grupo debe tener al menos 3 fichas");
            return;
        }
        
        modelo.crearGrupo(fichas);
    }

    // Inicializacion

    public void registrarJugador(String idJugador) {
        if (idJugador == null || idJugador.isBlank()) {
            vista.mostrarMensaje("ID de jugador inválido");
            return;
        }
        
        try {
            modelo.registrarJugador(idJugador);
            vista.mostrarMensaje("Jugador registrado: " + idJugador);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al registrar jugador: " + e.getMessage());
        }
    }

    public List<String> getJugadores() {
        try {
            return modelo.getJugadores();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener jugadores: " + e.getMessage());
            return List.of(); // Lista vacía
        }
    }

    // Consultas

    public boolean esTurnoDelJugador(String jugadorId) {
        return modelo.esTurnoDe(jugadorId);
    }

    public String getJugadorActualId() {
        try {
            return modelo.getJugadorActivoId();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener jugador actual: " + e.getMessage());
            return null;
        }
    }

    public int getTurnoActual() {
        try {
            return modelo.getTurnoActual();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener turno actual: " + e.getMessage());
            return -1;
        }
    }

    // Utils

    public void inicializarJuego(List<FichaDTO> manoJugador1, List<FichaDTO> manoJugador2, List<FichaDTO> pozoFichas) {
        if (manoJugador1 == null || manoJugador2 == null || pozoFichas == null) {
            vista.mostrarMensaje("Datos de inicialización inválidos");
            return;
        }
        
        modelo.inicializarJuego(manoJugador1, manoJugador2, pozoFichas);
    }
}