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
        vista.actualizar();
    }

    public void clickSeleccionaCasilla(String casillaId) {
        vista.actualizar();
    }

    public void crearGrupo(String jugadorId) {
        try {
            modelo.crearGrupo(new ArrayList<>(seleccionActualFichas), jugadorId);
            seleccionActualFichas.clear();
            vista.mostrarMensaje("Grupo creado correctamente.");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al crear grupo: " + e.getMessage());
        }
        vista.actualizar();
    }

    public void terminarTurno(String jugadorId) {
        try {
            modelo.terminarTurno(jugadorId);
            modelo.pasarTurno();
            vista.mostrarMensaje("Turno terminado para " + jugadorId);
        } catch (Exception e) {
            vista.mostrarMensaje("Error al terminar turno: " + e.getMessage());
        }
        vista.actualizar();
    }

    public void limpiarSeleccion() {
        seleccionActualFichas.clear();
        grupoSeleccionadoId = null;
        vista.actualizar();
    }

    public void grupoSeleccionado(String grupoId) {
        this.grupoSeleccionadoId = grupoId;
        vista.actualizar();
    }

    public void fichaSeleccionada(Ficha ficha) {
        if (ficha == null) {
            return;
        }
        seleccionActualFichas.add(ficha);
        vista.actualizar();
    }

    public void separarGrupo(String jugadorId) {
        try {
            modelo.separarGrupo(grupoSeleccionadoId, jugadorId);
            vista.mostrarMensaje("Grupo separado correctamente.");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al separar grupo: " + e.getMessage());
        }
        vista.actualizar();
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
        vista.actualizar();
    }

    public void cambiarFicha(String jugadorId) {
        try {
            modelo.cambiarFicha(jugadorId, grupoSeleccionadoId);
            vista.mostrarMensaje("Cambio de ficha realizado correctamente.");
        } catch (Exception e) {
            vista.mostrarMensaje("Error al cambiar ficha: " + e.getMessage());
        }
        vista.actualizar();
    }
}
