/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

/**
 *
 * @author Dana Chavez
 */
public class Grupo extends ContenedorFichas {

    public Grupo(String id) {
        super(id);
    }

    public void ordenar() {
        fichas.sort((f1, f2) -> {
            if (f1.isEsComodin() && !f2.isEsComodin()) return 1;
            if (!f1.isEsComodin() && f2.isEsComodin()) return -1;
            if (f1.isEsComodin() && f2.isEsComodin()) return 0;

            return Integer.compare(f1.getNumero(), f2.getNumero());
        });
    }
}
