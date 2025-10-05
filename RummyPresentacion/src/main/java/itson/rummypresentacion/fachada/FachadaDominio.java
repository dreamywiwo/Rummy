/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.fachada;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class FachadaDominio {
    
    public boolean validarSecuencia(List<Integer> numero, List<String> color) throws Exception {
        if (numero.isEmpty() || color.isEmpty()) {
            throw new Exception("La lista vacia.");
        }

        String colorBase = color.get(0);
        for (String colores : color) {
            if (!colores.equalsIgnoreCase(colorBase)) {
                return false;
            }
        }

        List<Integer> ordenar = new ArrayList<>(numero);
        Collections.sort(ordenar);
        for (int i = 1; i < ordenar.size(); i++) {
            if (ordenar.get(i) != ordenar.get(i - 1) + 1) {
                return false;
            }
        }
        return true;

    }

    public boolean validarNumeroIgual(List<Integer> numero, List<String> color) throws Exception {
        if (numero.isEmpty() || color.isEmpty()) {
            return false;
        }
        int numeroBase = numero.get(0);
        for(int num:numero){
            if(num!=numeroBase){
                return false;
            }
        }
        return true;
    }
}
