/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.fachada;

import itson.rummypresentacion.DTOs.FichaDTO;
import itson.rummypresentacion.DTOs.GrupoDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class FachadaDominio implements IFachadaDominio {

    private final List<String> jugadores = new ArrayList<>();
    private int numeroTurno = 0;
    private String idJugadorActual = null;
    private final List<String> pozoFichas = new ArrayList<>();
    private final List<String> fichasA = new ArrayList<>();
    private final List<String> fichasB = new ArrayList<>();

    @Override
    public void terminarTurno(String idJugador) throws Exception {
        if (!esTurnoDelJugador(idJugador)) {
            throw new Exception("No es tu turno, jugador " + idJugador);
        }
        System.out.println("Turno finalizado, jugador: " + getJugadorActualId() + ", sigue");
    }

    @Override
    public void tomarFicha(String jugadorId) throws Exception {
        if (!esTurnoDelJugador(jugadorId)) {
            throw new Exception("No es tu turno, jugador " + jugadorId);
        }
        if (pozoFichas.isEmpty()) {
            throw new Exception("No hay fichas en el pozo");
        }
        String ficha = pozoFichas.remove(0);
        if (jugadorId.equals("Jugador1")) {
            fichasA.add(ficha);
        } else {
            fichasB.add(ficha);
        }
    }

    @Override
    public boolean pasarTurno() {
        if (pozoFichas.isEmpty()) {
            numeroTurno = (numeroTurno + 1) % jugadores.size();
            return false;
        }
        numeroTurno = (numeroTurno + 1) % jugadores.size();
        return true;
    }

    @Override
    public void registrarJugador(String idJugador) {
        if (idJugador == null || idJugador.isBlank()) {
            return;
        }
        if (!jugadores.contains(idJugador)) {
            jugadores.add(idJugador);
            if (idJugadorActual == null) {
                idJugadorActual = idJugador;
            }
            System.out.println("Jugador registrado: " + idJugador);
        }
    }
    
    @Override
    public GrupoDTO crearGrupo(List<FichaDTO> fichas) throws Exception {
        if (fichas == null) {
            throw new Exception("No hay fichas para ese grupo.");
        }
        boolean esSecuencia = validarSecuencia(fichas);
        boolean esMismoNumero = validarNumeroIgual(fichas);
        if (!esSecuencia && !esMismoNumero) {
            throw new Exception("Grupo inválido: debe ser secuencia del mismo color o del mismo número con colores distintos.");
        }
        return new GrupoDTO(new ArrayList<>(fichas), true);
    }
    
    public GrupoDTO agregarFichaAGrupo(FichaDTO ficha, GrupoDTO grupo)throws Exception{
        if (grupo == null) {
            throw new Exception("Debe de existir un grupo al cual anadir la ficha.");
        }
        List<FichaDTO> fichas = grupo.getFichas();
        fichas.add(ficha);
        
        boolean esSecuencia = validarSecuencia(fichas);
        boolean esMismoNumero = validarNumeroIgual(fichas);
        if (!esSecuencia && !esMismoNumero) {
            throw new Exception("Grupo inválido: debe ser secuencia del mismo color o del mismo número con colores distintos.");
        }
        
        return new GrupoDTO(new ArrayList<>(fichas), true);
    }

    public GrupoDTO eliminarFichaDeGrupo(FichaDTO ficha, GrupoDTO grupo)throws Exception{
        
        if (grupo == null || grupo.getFichas().size() < 4) {
            throw new Exception("Un grupo debe tener al menos 4 fichas para poder eliminar una.");
        }
        
        List<FichaDTO> fichas = grupo.getFichas();
        if (!fichas.contains(ficha)) {
            throw new Exception("el grupo no contiene la ficha seleccionada");
        }
        
        fichas.remove(ficha);
        boolean esSecuencia = validarSecuencia(fichas);
        boolean esMismoNumero = validarNumeroIgual(fichas);
        if (!esSecuencia && !esMismoNumero) {
            throw new Exception("Grupo inválido: debe ser secuencia del mismo color o del mismo número con colores distintos.");
        }
        
        return new GrupoDTO(new ArrayList<>(fichas), true);
    }
    
    @Override
    public int getNumeroTurno() {
        return numeroTurno;
    }

    @Override
    public String getJugadorActualId() {
        return jugadores.get(numeroTurno);
    }

    @Override
    public boolean esTurnoDelJugador(String idJugador) {
        return jugadores.get(numeroTurno).equals(idJugador);
    }

    public List<String> getJugadores() {
        return new ArrayList<>(jugadores);
    }

    
    /* Validaciones grupos */
    public boolean validarSecuencia(List<FichaDTO> fichas) throws Exception {
        if (fichas == null || fichas.isEmpty()) {
            throw new Exception("La lista está vacía.");
        }
        java.awt.Color colorBase = fichas.get(0).getColor();
        for (FichaDTO f : fichas) {
            if (!f.getColor().equals(colorBase)) {
                return false;
            }
        }
        List<Integer> numeros = new ArrayList<>();
        for (FichaDTO f : fichas) {
            numeros.add(f.getNumero());
        }

        Collections.sort(numeros);
        for (int i = 1; i < numeros.size(); i++) {
            if (numeros.get(i) != numeros.get(i - 1) + 1) {
                return false;
            }
        }
        return true;
    }

    public boolean validarNumeroIgual(List<FichaDTO> fichas) throws Exception {
        if (fichas == null || fichas.isEmpty()) {
            throw new Exception("La lista está vacía.");
        }
        int numeroBase = fichas.get(0).getNumero();
        for (FichaDTO f : fichas) {
            if (f.getNumero() != numeroBase) {
                return false;
            }
        }
        for (int i = 0; i < fichas.size(); i++) {
            for (int j = i + 1; j < fichas.size(); j++) {
                if (fichas.get(i).getColor().equals(fichas.get(j).getColor())) {
                    return false;
                }
            }
        }
        return true;
    }

}
