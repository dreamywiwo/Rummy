/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package itson.dominiorummy.facade;

import itson.rummydtos.FichaDTO;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public interface IDominio {

    public void actualizarGrupo(String grupoId, List<FichaDTO> nuevasFichas);
    
}
