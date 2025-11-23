/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.util;

import itson.dominiorummy.entidades.Ficha;
import java.util.List;

/**
 *
 * @author victoria
 */
public class ValidadorGrupo {

    public static boolean esValido(List<Ficha> fichas) {
        if (fichas.size() < 3) {
            return false;
        }
        return true; 
    }
}

