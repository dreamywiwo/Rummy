/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummypresentacion.DTOs;

import entidades.Ficha;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adrián
 */
public class JugadorDTO {
    private String id;
    private String nombre;
    private String avatar;
    private List<Ficha> mano;

    public JugadorDTO() {
        this.mano = new ArrayList<>();
    }

    public JugadorDTO(String id, String nombre, String avatar, List<Ficha> mano) {
        this.id = id;
        this.nombre = nombre;
        this.avatar = avatar;
        this.mano = mano;
    }

    public JugadorDTO(String nombre, String avatar, List<Ficha> mano) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.mano = mano;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<Ficha> getMano() {
        return mano;
    }
    
    public int getManoNumero(){
        int cont=0;
        for(int i =0;i<mano.size();i++){
            cont++;
        }
        return cont;
    }

    public void setMano(List<Ficha> mano) {
        this.mano = mano;
    }

    @Override
    public String toString() {
        return "Jugador{" + "id=" + id + ", nombre=" + nombre + ", avatar=" + avatar + '}';
    }
  
}
