/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package itson.rummypresentacion.modelo;

import entidades.EstadoJuego;
import entidades.Ficha;
import entidades.Grupo;
import entidades.Jugador;
import entidades.Movimiento;
import entidades.Tablero;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jrasc
 */
public class NewMain {

    private static String fmtFicha(Ficha f) {
        return "{" + f.getValor() + "," + f.getColor() + "}";
    }

    private static void imprimirMano(String titulo, Jugador j) {
        System.out.println(titulo + " (" + j.getMano().size() + " fichas):");
        StringBuilder sb = new StringBuilder("  ");
        for (int i = 0; i < j.getMano().size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(fmtFicha(j.getMano().get(i)));
        }
        System.out.println(sb);
    }

    private static void imprimirTablero(Tablero t) {
        System.out.println("Grupos en el tablero (" + t.getGrupos().size() + "):");
        for (int gi = 0; gi < t.getGrupos().size(); gi++) {
            Grupo g = t.getGrupos().get(gi);
            StringBuilder sb = new StringBuilder("  G" + (gi + 1) + ": [");
            for (int i = 0; i < g.getFichas().size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append(fmtFicha(g.getFichas().get(i)));
            }
            sb.append("]");
            System.out.println(sb);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Modelo m = new Modelo();
            Tablero tablero = new Tablero();
            EstadoJuego estado = new EstadoJuego();
            estado.setTablero(tablero);

            Ficha f1 = new Ficha("4", "verde", null);
            Ficha f2 = new Ficha("4", "rojo", null);
            Ficha f3 = new Ficha("4", "amarillo", null);
            Ficha f4 = new Ficha("9", "azul", null);
            Ficha f5 = new Ficha("9", "rojo", null);
            Ficha f6 = new Ficha("9", "verde", null);
            Ficha f7 = new Ficha("7", "azul", null);
            Ficha f8 = new Ficha("12", "verde", null);

            Jugador jugador = new Jugador();
            jugador.setNombre("Kenshin");
            jugador.setMano(new ArrayList<>(List.of(f1, f2, f3, f4, f5, f6, f7, f8)));
            imprimirMano("Mano inicial de " + jugador.getNombre(), jugador);

            Grupo grupoSet1 = new Grupo();
            grupoSet1.getFichas().add(f1);
            grupoSet1.getFichas().add(f2);
            grupoSet1.getFichas().add(f3);
            System.out.println("SET válido 1 => " + m.validarGrupoColor(grupoSet1));

            Grupo grupoSet2 = new Grupo();
            grupoSet2.getFichas().add(f4);
            grupoSet2.getFichas().add(f5);
            grupoSet2.getFichas().add(f6);
            System.out.println("SET válido 2 => " + m.validarGrupoColor(grupoSet2));

            Grupo grupoSetBad1 = new Grupo();
            grupoSetBad1.getFichas().add(new Ficha("4", "rojo", null));
            grupoSetBad1.getFichas().add(new Ficha("4", "rojo", null));
            grupoSetBad1.getFichas().add(new Ficha("4", "verde", null));
            System.out.println("SET inválido 1 => " + m.validarGrupoColor(grupoSetBad1));

            Grupo grupoSetBad2 = new Grupo();
            grupoSetBad2.getFichas().add(new Ficha("4", "verde", null));
            grupoSetBad2.getFichas().add(new Ficha("5", "rojo", null));
            grupoSetBad2.getFichas().add(new Ficha("4", "amarillo", null));
            System.out.println("SET inválido 2 => " + m.validarGrupoColor(grupoSetBad2));

            Movimiento movSet = new Movimiento();
            movSet.setTipo("bajar grupo");
            movSet.setJugador(jugador);
            movSet.setGrupo(grupoSet1);
            m.registrarMovimiento(estado, movSet);

            System.out.println("SET bajado correctamente.");
            imprimirTablero(estado.getTablero());
            imprimirMano("Mano después de bajar SET", jugador);

            Grupo grupoSetEnTablero = estado.getTablero().getGrupos().get(0);
            Ficha fExtSetOk = new Ficha("4", "azul", null);
            jugador.getMano().add(fExtSetOk);
            Movimiento movExtSetOk = new Movimiento();
            movExtSetOk.setTipo("extender grupo");
            movExtSetOk.setJugador(jugador);
            movExtSetOk.setGrupo(grupoSetEnTablero);
            movExtSetOk.setFicha(fExtSetOk);
            m.registrarMovimiento(estado, movExtSetOk);
            System.out.println("SET extendido correctamente.");
            imprimirTablero(estado.getTablero());
            imprimirMano("Mano tras extender SET (válido)", jugador);

            Ficha fExtSetBad = new Ficha("4", "rojo", null);
            jugador.getMano().add(fExtSetBad);
            Movimiento movExtSetBad = new Movimiento();
            movExtSetBad.setTipo("extender grupo");
            movExtSetBad.setJugador(jugador);
            movExtSetBad.setGrupo(grupoSetEnTablero);
            movExtSetBad.setFicha(fExtSetBad);
            try {
                m.registrarMovimiento(estado, movExtSetBad);
                System.out.println("Error: extendió SET inválido.");
            } catch (Exception e) {
                System.out.println("OK (rechazó extender SET inválido): " + e.getMessage());
            }

            Ficha s1 = new Ficha("3", "rojo", null);
            Ficha s2 = new Ficha("4", "rojo", null);
            Ficha s3 = new Ficha("5", "rojo", null);
            Ficha s4 = new Ficha("10", "azul", null);
            Ficha s5 = new Ficha("11", "azul", null);
            Ficha s6 = new Ficha("12", "azul", null);
            Ficha s7 = new Ficha("7", "verde", null);

            Jugador jugadorSerie = new Jugador();
            jugadorSerie.setNombre("Tomoe");
            jugadorSerie.setMano(new ArrayList<>(List.of(s1, s2, s3, s4, s5, s6, s7)));
            imprimirMano("\nMano inicial de " + jugadorSerie.getNombre(), jugadorSerie);

            Grupo grupoSerie1 = new Grupo();
            grupoSerie1.getFichas().add(s1);
            grupoSerie1.getFichas().add(s2);
            grupoSerie1.getFichas().add(s3);
            System.out.println("SERIE válida 1 => " + m.validarGrupoConsecutivo(grupoSerie1));

            Grupo grupoSerie2 = new Grupo();
            grupoSerie2.getFichas().add(s4);
            grupoSerie2.getFichas().add(s5);
            grupoSerie2.getFichas().add(s6);
            System.out.println("SERIE válida 2 => " + m.validarGrupoConsecutivo(grupoSerie2));

            Grupo grupoSerieBad1 = new Grupo();
            grupoSerieBad1.getFichas().add(new Ficha("3", "rojo", null));
            grupoSerieBad1.getFichas().add(new Ficha("5", "rojo", null));
            grupoSerieBad1.getFichas().add(new Ficha("6", "rojo", null));
            System.out.println("SERIE inválida 1 => " + m.validarGrupoConsecutivo(grupoSerieBad1));

            Grupo grupoSerieBad2 = new Grupo();
            grupoSerieBad2.getFichas().add(new Ficha("3", "rojo", null));
            grupoSerieBad2.getFichas().add(new Ficha("4", "azul", null));
            grupoSerieBad2.getFichas().add(new Ficha("5", "rojo", null));
            System.out.println("SERIE inválida 2 => " + m.validarGrupoConsecutivo(grupoSerieBad2));

            Movimiento movSerie = new Movimiento();
            movSerie.setTipo("bajar grupo");
            movSerie.setJugador(jugadorSerie);
            movSerie.setGrupo(grupoSerie1);
            m.registrarMovimiento(estado, movSerie);

            System.out.println("SERIE bajada correctamente.");
            imprimirTablero(estado.getTablero());
            imprimirMano("Mano después de bajar SERIE", jugadorSerie);

            Grupo grupoSerieEnTablero = estado.getTablero().getGrupos().get(1);
            Ficha fExtSerieOk = new Ficha("6", "rojo", null);
            jugadorSerie.getMano().add(fExtSerieOk);
            Movimiento movExtSerieOk = new Movimiento();
            movExtSerieOk.setTipo("extender grupo");
            movExtSerieOk.setJugador(jugadorSerie);
            movExtSerieOk.setGrupo(grupoSerieEnTablero);
            movExtSerieOk.setFicha(fExtSerieOk);
            m.registrarMovimiento(estado, movExtSerieOk);
            System.out.println("SERIE extendida correctamente.");
            imprimirTablero(estado.getTablero());
            imprimirMano("Mano tras extender SERIE (válido)", jugadorSerie);

            Ficha fExtSerieBad = new Ficha("8", "rojo", null);
            jugadorSerie.getMano().add(fExtSerieBad);
            Movimiento movExtSerieBad = new Movimiento();
            movExtSerieBad.setTipo("extender grupo");
            movExtSerieBad.setJugador(jugadorSerie);
            movExtSerieBad.setGrupo(grupoSerieEnTablero);
            movExtSerieBad.setFicha(fExtSerieBad);
            try {
                m.registrarMovimiento(estado, movExtSerieBad);
                System.out.println("Error: extendió SERIE inválida.");
            } catch (Exception e) {
                System.out.println("OK (rechazó extender SERIE inválida): " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

}
