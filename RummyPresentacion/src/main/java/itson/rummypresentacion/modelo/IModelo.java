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
    
    void crearGrupo(List<Ficha> fichas, String jugadorId) throws Exception;
    void terminarTurno(String jugadorId) throws Exception;
    void pasarTurno() throws Exception;
    void separarGrupo(String grupoId, String jugadorId) throws Exception;
    void sustituirFicha(String jugadorId, String grupoId, Ficha fichaNueva) throws Exception;
    void cambiarFicha(String jugadorId, String grupoId) throws Exception;
}
