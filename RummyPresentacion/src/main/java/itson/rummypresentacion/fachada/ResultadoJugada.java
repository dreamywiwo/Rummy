/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.fachada;

import itson.rummypresentacion.modelo.IModelo;

public class ResultadoJugada {
    private final boolean exitoso;
    private final String mensaje;
    
    public ResultadoJugada(boolean exitoso, String mensaje) {
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }

    public boolean esExitoso() {
        return exitoso;
    }

    public String getMensaje() {
        return mensaje;
    }

}
    
