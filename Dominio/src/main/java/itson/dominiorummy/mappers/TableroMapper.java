/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.dominiorummy.mappers;

import itson.dominiorummy.entidades.Ficha;
import itson.dominiorummy.entidades.FichaPlaced;
import itson.dominiorummy.entidades.Grupo;
import itson.dominiorummy.entidades.Tablero;
import itson.rummydtos.FichaDTO;
import itson.rummydtos.GrupoDTO;
import itson.rummydtos.TableroDTO;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class TableroMapper {

    public static TableroDTO toDTO(Tablero tablero) {
        List<GrupoDTO> gruposDTO = new ArrayList<>();
        for (Grupo grupo : tablero.getGruposEnMesa()) {
            gruposDTO.add(grupoaDTO(grupo));
        }
        return new TableroDTO(gruposDTO);
    }

    private static GrupoDTO grupoaDTO(Grupo grupo) {
        List<Ficha> fichas = grupo.getFichas().stream().map(FichaPlaced::getFicha).toList();
        List<FichaDTO> fichasDTO = FichaMapper.toDTO(fichas);
        return new GrupoDTO(grupo.getId(), fichasDTO, true);
    }
}
