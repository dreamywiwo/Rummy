/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.vista;

import itson.rummypresentacion.modelo.IModelo;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class UI_Grupo extends ComponenteBase{
    private List<UI_Ficha> fichas;
    private UI_Tablero tablero;
    private List<UI_Ficha> fichasSeleccionadas;

    public UI_Grupo(String id) {
        super(id);
        this.fichas = new ArrayList<>();
        this.fichasSeleccionadas = new ArrayList<>();
        inicializarComponente();
    }
    
    private void inicializarComponente() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        setBackground(new Color(39, 41, 72));
        setOpaque(true);
    }

    @Override
    public void actualizar(IModelo modelo) {
        // Primero limpiar el grupo actual

        // TODO: Obtener las fichas del modelo para este grupo específico
        
        // TODO: Crear y agregar las fichas UI

        
        // Propagar la actualización a los componentes hijos (fichas)
//        super.actualizar(modelo);
        
        System.out.println("UI_Grupo '" + getId() + "' actualizado con " + fichas.size() + " fichas");
        
        
    }

    private List<String> obtenerFichasDesdeModelo(IModelo modelo) {
        // TODO: en si regresaria una lista de fichas
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody  
    }


    public void setTablero(UI_Tablero tablero) {
        this.tablero = tablero;
    }

    public void agregarFicha(UI_Ficha ficha) {
        configurarFichaParaSeleccion(ficha);
        fichas.add(ficha);
        agregarComponente(ficha); // Agregar al composite
        add(ficha); // Agregar al panel Swing
    }

    private void configurarFichaParaSeleccion(UI_Ficha ficha) {
        ficha.addActionListener(e -> {
            if (ficha.isSelected()) {
                fichasSeleccionadas.add(ficha);
            } else {
                fichasSeleccionadas.remove(ficha);
            }
        });
    }

    public List<UI_Ficha> getFichasSeleccionadas() {
        return new ArrayList<>(fichasSeleccionadas);
    }

    public void limpiarSeleccion() {
        for (UI_Ficha ficha : fichasSeleccionadas) {
            ficha.setSelected(false);
        }
        fichasSeleccionadas.clear();
    }

    public void removerFichasSeleccionadas() {
        for (UI_Ficha ficha : fichasSeleccionadas) {
            fichas.remove(ficha);
            removerComponente(ficha);
            remove(ficha);
        }
        fichasSeleccionadas.clear();
    }

    public void limpiarGrupo() {
        for (UI_Ficha ficha : fichas) {
            removerComponente(ficha);
            remove(ficha);
        }
        fichas.clear();
        fichasSeleccionadas.clear();
    }

    public List<UI_Ficha> getFichas() {
        return new ArrayList<>(fichas);
    }
}
