/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummydtos;

import java.util.List;

/**
 *
 * @author jrasc
 */
public class GrupoDTO {

    private final List<FichaDTO> fichas;
    private final boolean valido;

    public GrupoDTO(List<FichaDTO> fichas, boolean valido) {
        this.fichas = fichas;
        this.valido = valido;
    }

    public List<FichaDTO> getFichas() {
        return fichas;
    }

    public boolean isValido() {
        return valido;
    }

}
