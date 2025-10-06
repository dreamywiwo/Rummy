/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.rummypresentacion.fachada;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import java.awt.Point;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public interface IFachadaDominio {

    public void terminarTurno(String jugadorId) throws Exception;
    public void tomarFicha(String jugadorId) throws Exception;
    public int getNumeroTurno();
    public String getJugadorActualId();
    public boolean esTurnoDelJugador(String jugadorId);
    public boolean pasarTurno();
    public void registrarJugador(String idJugador);
    public List<String> getJugadores();
    public GrupoDTO crearGrupo(List<FichaDTO> fichas) throws Exception;
    public void inicializarJuego(List<FichaDTO> manoJugador1, List<FichaDTO> manoJugador2, List<FichaDTO> pozo);
    public ResultadoJugada colocarFichas(String jugadorId, List<FichaDTO> fichas, Point posicion) throws Exception;
    public List<FichaDTO> obtenerManoJugadorActual();
    public List<GrupoDTO> obtenerGruposTablero();
    List<FichaDTO> obtenerManoJugador(String jugadorId);
    boolean jugadorYaBajo30(String jugadorId);

}
