/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package itson.rummyeventos.actualizaciones;

import itson.rummydtos.FichaDTO;
import itson.rummyeventos.base.EventBase;
import java.util.List;

/**
 *
 * @author Dana Chavez
 */
public class ManoActualizadaEvent extends EventBase {
    
    private List<FichaDTO> manoSnapshot;
    
    public static final String TOPIC = "actualizaciones.estado";
    public static final String EVENT_TYPE = "mano.actualizada";
    
    public ManoActualizadaEvent(List<FichaDTO> manoSnapshot) {
        super(TOPIC, EVENT_TYPE);
        this.manoSnapshot = manoSnapshot;
    }

    public List<FichaDTO> getManoSnapshot() {
        return manoSnapshot;
    }

    public void setManoSnapshot(List<FichaDTO> manoSnapshot) {
        this.manoSnapshot = manoSnapshot;
    }
        
}
