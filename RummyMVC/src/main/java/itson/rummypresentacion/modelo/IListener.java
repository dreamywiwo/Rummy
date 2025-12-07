/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.modelo;

import itson.rummydtos.FichaDTO;
import itson.rummydtos.JugadorDTO;
import itson.rummydtos.TableroDTO;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public interface IListener {

    public void terminoTurno(String jugadorActivoId);
    public void recibirTablero(TableroDTO tableroDTO);
    public void recibirMano(List<FichaDTO> mano);
    public void recibirSopa(int cantidad);
    public void recibirError(String mensaje);

    public void marcarJuegoTerminado(JugadorDTO ganador);

    public void resaltarGrupoInvalido(String grupoId);

    public void actualizarFichasOponente(String jugadorId, int size);
}
