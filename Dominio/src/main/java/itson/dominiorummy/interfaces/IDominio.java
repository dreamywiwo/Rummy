/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.dominiorummy.interfaces;

import java.util.List;
import itson.rummydtos.FichaDTO;

/**
 *
 * @author victoria
 */

public interface IDominio {
    
    void crearGrupo(String jugadorId, List<FichaDTO> fichasDTO);
}