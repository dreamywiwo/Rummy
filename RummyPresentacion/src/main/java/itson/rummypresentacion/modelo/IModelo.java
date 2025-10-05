/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.modelo;

import entidades.Ficha;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public interface IModelo {

    public void crearGrupo(List<Ficha> fichas, String jugadorId) throws Exception;

    public void terminarTurno(String jugadorId) throws Exception;

    public void pasarTurno() throws Exception;

    public void suscribir(IObserver observer);

    public void separarGrupo(String grupoId, String jugadorId) throws Exception;

    public void sustituirFicha(String jugadorId, String grupoId, Ficha fichaNueva) throws Exception;

    public void cambiarFicha(String jugadorId, String grupoId) throws Exception;

    public void tomarFicha(String jugadorId) throws Exception;

    public String getJugadorActualId() throws Exception;

    public boolean esTurnoDelJugador(String idJugador);

    public int getTurnoNumero();

    public void registrarJugador(String idJugador);

    public List<String> getJugadores();
}
