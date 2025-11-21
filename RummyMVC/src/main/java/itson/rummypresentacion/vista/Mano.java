/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import java.util.Collections;

/**
 *
 * @author Dana Chavez
 */
public class Mano extends ContenedorFichas {

    public Mano(String jugadorId) {
        super("mano_" + jugadorId);
    }

    public void ordenar() {
        Collections.sort(fichas, (f1, f2) -> {

            if (f1.isEsComodin() && !f2.isEsComodin()) return 1;
            if (!f1.isEsComodin() && f2.isEsComodin()) return -1;

            if (f1.getNumero() != f2.getNumero()) {
                return f1.getNumero() - f2.getNumero();
            }

            return f1.getColor().compareTo(f2.getColor());
        });
    }
}
