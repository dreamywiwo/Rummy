/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummydtos;

import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class JugadorDTO {

    private String id;
    private List<FichaDTO> fichasMano;

    public JugadorDTO() {
    }

    public JugadorDTO(String id, List<FichaDTO> fichasMano) {
        this.id = id;
        this.fichasMano = fichasMano;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FichaDTO> getFichasMano() {
        return fichasMano;
    }

    public void setFichasMano(List<FichaDTO> fichasMano) {
        this.fichasMano = fichasMano;
    }

}
