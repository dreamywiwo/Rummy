/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.controlador;

import entidades.Ficha;
import itson.rummypresentacion.modelo.IModelo;
import itson.rummypresentacion.vista.UI_TurnoJugador;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class ControlTurno {

    private final UI_TurnoJugador vista;
    private final IModelo modelo;

    private final List<Ficha> seleccionActualFichas = new ArrayList<>();
    private String grupoSeleccionadoId;

    public ControlTurno(UI_TurnoJugador vista, IModelo modelo) {
        this.vista = vista;
        this.modelo = modelo;
    }

    public void clickSeleccionaFichas(Ficha ficha) {
        if (ficha == null) {
            return;
        }
        seleccionActualFichas.add(ficha);

    }

    public void clickSeleccionaCasilla(String casillaId) {

    }

    public void crearGrupo(String jugadorId) {
        try {
            modelo.crearGrupo(new ArrayList<>(seleccionActualFichas), jugadorId);
            seleccionActualFichas.clear();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al crear grupo: " + e.getMessage());
        }
    }

    public void terminarTurno(String jugadorId) {
        try {
            modelo.terminarTurno(jugadorId);
            modelo.pasarTurno();
        } catch (Exception e) {
        }

    }

    public void limpiarSeleccion() {
        seleccionActualFichas.clear();
        grupoSeleccionadoId = null;

    }

    public void grupoSeleccionado(String grupoId) {
        this.grupoSeleccionadoId = grupoId;

    }

    public void fichaSeleccionada(Ficha ficha) {
        if (ficha == null) {
            return;
        }
        seleccionActualFichas.add(ficha);

    }

    public void separarGrupo(String jugadorId) {
        try {
            modelo.separarGrupo(grupoSeleccionadoId, jugadorId);
        } catch (Exception e) {
        }

    }

    public void sustituirFicha(String jugadorId) {
        if (seleccionActualFichas.isEmpty()) {
            vista.mostrarMensaje("Debe seleccionar una ficha para sustituir.");
            return;
        }
        Ficha fichaNueva = seleccionActualFichas.get(seleccionActualFichas.size() - 1);
        try {
            modelo.sustituirFicha(jugadorId, grupoSeleccionadoId, fichaNueva);
            vista.mostrarMensaje("Ficha sustituida correctamente.");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al sustituir ficha: " + e.getMessage());
        }

    }

    public void cambiarFicha(String jugadorId) {
        try {
            modelo.cambiarFicha(jugadorId, grupoSeleccionadoId);
            vista.mostrarMensaje("Cambio de ficha realizado correctamente.");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al cambiar ficha: " + e.getMessage());
        }

    }

    public void tomarFicha(String idJugador) {
        try {
            modelo.tomarFicha(idJugador);
            modelo.pasarTurno();

        } catch (Exception e) {
            vista.mostrarMensaje("Error al tomar ficha: " + e.getMessage());
        }
    }

    public void registrarJugador(String idJugador) {
        try {
            modelo.registrarJugador(idJugador);
            System.out.println("Jugador registrado: " + idJugador);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al registrar jugador: " + e.getMessage());
        }
    }

    public List<String> getJugadores() {
        try {
            return modelo.getJugadores();
        } catch (Exception e) {
            vista.mostrarMensaje("Error al obtener jugadores: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
